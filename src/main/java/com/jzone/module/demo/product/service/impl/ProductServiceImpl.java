package com.jzone.module.demo.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzone.common.pojo.PageResult;
import com.jzone.common.util.ExcelUtil;
import com.jzone.framework.exception.BusinessException;
import com.jzone.module.demo.product.mapper.ProductMapper;
import com.jzone.module.demo.product.pojo.dto.AddProductDTO;
import com.jzone.module.demo.product.pojo.dto.EditProductDTO;
import com.jzone.module.demo.product.pojo.dto.QueryProductDTO;
import com.jzone.module.demo.product.pojo.po.ProductPO;
import com.jzone.module.demo.product.pojo.vo.ProductVO;
import com.jzone.module.demo.product.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductPO> implements ProductService {

    @Override
    public PageResult<ProductVO> pageProduct(QueryProductDTO dto) {
        var page = this.lambdaQuery()
                .like(StrUtil.isNotBlank(dto.getProductName()), ProductPO::getProductName, dto.getProductName())
                .like(StrUtil.isNotBlank(dto.getProductCode()), ProductPO::getProductCode, dto.getProductCode())
                .eq(StrUtil.isNotBlank(dto.getCategory()), ProductPO::getCategory, dto.getCategory())
                .eq(dto.getStatus() != null, ProductPO::getStatus, dto.getStatus())
                .page(dto.toPage());
        List<ProductVO> vos = BeanUtil.copyToList(page.getRecords(), ProductVO.class);
        return PageResult.of(page, vos);
    }

    @Override
    public ProductVO getProductById(Long id) {
        ProductPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("商品不存在");
        }
        return BeanUtil.copyProperties(po, ProductVO.class);
    }

    @Override
    public void addProduct(AddProductDTO dto) {
        // 编号唯一性校验
        long count = this.lambdaQuery().eq(ProductPO::getProductCode, dto.getProductCode()).count();
        if (count > 0) {
            throw new BusinessException("商品编号已存在");
        }
        ProductPO po = BeanUtil.copyProperties(dto, ProductPO.class);
        this.save(po);
    }

    @Override
    public void editProduct(EditProductDTO dto) {
        ProductPO po = this.getById(dto.getId());
        if (po == null) {
            throw new BusinessException("商品不存在");
        }
        // 编号唯一性校验（排除自身）
        if (StrUtil.isNotBlank(dto.getProductCode())) {
            long count = this.lambdaQuery()
                    .eq(ProductPO::getProductCode, dto.getProductCode())
                    .ne(ProductPO::getId, dto.getId())
                    .count();
            if (count > 0) {
                throw new BusinessException("商品编号已存在");
            }
        }
        BeanUtil.copyProperties(dto, po, "id");
        this.updateById(po);
    }

    @Override
    public void deleteProduct(Long id) {
        ProductPO po = this.getById(id);
        if (po == null) {
            throw new BusinessException("商品不存在");
        }
        this.removeById(id);
    }

    @Override
    public void exportProduct(QueryProductDTO dto, HttpServletResponse response) {
        List<ProductPO> list = this.lambdaQuery()
                .like(StrUtil.isNotBlank(dto.getProductName()), ProductPO::getProductName, dto.getProductName())
                .like(StrUtil.isNotBlank(dto.getProductCode()), ProductPO::getProductCode, dto.getProductCode())
                .eq(StrUtil.isNotBlank(dto.getCategory()), ProductPO::getCategory, dto.getCategory())
                .eq(dto.getStatus() != null, ProductPO::getStatus, dto.getStatus())
                .list();
        List<ProductVO> vos = BeanUtil.copyToList(list, ProductVO.class);

        List<String> headers = List.of("商品编号", "商品名称", "分类", "价格", "库存", "状态", "备注");
        List<List<String>> rows = new ArrayList<>();
        for (ProductVO vo : vos) {
            List<String> row = new ArrayList<>();
            row.add(vo.getProductCode());
            row.add(vo.getProductName());
            row.add(vo.getCategory());
            row.add(vo.getPrice() != null ? vo.getPrice().toPlainString() : "");
            row.add(vo.getStock() != null ? vo.getStock().toString() : "");
            row.add(vo.getStatus() != null && vo.getStatus() == 1 ? "上架" : "下架");
            row.add(vo.getRemark());
            rows.add(row);
        }
        ExcelUtil.exportXlsx(response, "商品数据", "商品", headers, rows);
    }

    @Override
    public void importProduct(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要导入的文件");
        }
        String filename = file.getOriginalFilename();
        if (StrUtil.isBlank(filename) || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            throw new BusinessException("仅支持 Excel 文件");
        }

        List<List<String>> rows;
        try {
            rows = ExcelUtil.readXlsx(file.getInputStream());
        } catch (Exception e) {
            throw new BusinessException("文件读取失败：" + e.getMessage());
        }
        if (CollUtil.isEmpty(rows) || rows.size() < 2) {
            throw new BusinessException("导入数据为空");
        }

        List<ProductPO> toInsert = new ArrayList<>();
        Set<String> seenCodes = new HashSet<>();

        // 跳过表头（第0行）
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (CollUtil.isEmpty(row)) continue;
            ProductPO po = new ProductPO();
            po.setProductCode(StrUtil.trimToNull(getCell(row, 0)));
            po.setProductName(StrUtil.trimToNull(getCell(row, 1)));
            po.setCategory(StrUtil.trimToNull(getCell(row, 2)));
            String priceStr = StrUtil.trimToNull(getCell(row, 3));
            if (StrUtil.isNotBlank(priceStr)) {
                try {
                    po.setPrice(new BigDecimal(priceStr));
                } catch (NumberFormatException e) {
                    throw new BusinessException("第" + (i + 1) + "行价格格式错误");
                }
            }
            String stockStr = StrUtil.trimToNull(getCell(row, 4));
            if (StrUtil.isNotBlank(stockStr)) {
                try {
                    po.setStock(Integer.parseInt(stockStr));
                } catch (NumberFormatException e) {
                    throw new BusinessException("第" + (i + 1) + "行库存格式错误");
                }
            }
            String statusStr = StrUtil.trimToNull(getCell(row, 5));
            if ("下架".equals(statusStr)) {
                po.setStatus(0);
            } else {
                po.setStatus(1);
            }
            po.setRemark(StrUtil.trimToNull(getCell(row, 6)));

            // 基础校验
            if (StrUtil.isBlank(po.getProductCode()) || StrUtil.isBlank(po.getProductName())) {
                throw new BusinessException("第" + (i + 1) + "行商品编号或名称不能为空");
            }

            // Excel 内部重复编号校验
            if (seenCodes.contains(po.getProductCode())) {
                throw new BusinessException("第" + (i + 1) + "行商品编号与前面行重复：" + po.getProductCode());
            }
            seenCodes.add(po.getProductCode());
            toInsert.add(po);
        }

        if (CollUtil.isEmpty(toInsert)) {
            throw new BusinessException("没有可导入的有效数据");
        }

        // 一次性批量查库，消除循环 N+1 查询
        List<String> existingCodes = this.lambdaQuery()
                .in(ProductPO::getProductCode, seenCodes)
                .select(ProductPO::getProductCode)
                .list()
                .stream()
                .map(ProductPO::getProductCode)
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(existingCodes)) {
            throw new BusinessException("导入失败，以下商品编号已存在：" + String.join(", ", existingCodes));
        }

        this.saveBatch(toInsert);
    }

    @Override
    public void downloadTemplate(HttpServletResponse response) {
        List<String> headers = List.of("商品编号*", "商品名称*", "分类", "价格", "库存", "状态", "备注");
        List<List<String>> rows = new ArrayList<>();
        List<String> sample = new ArrayList<>();
        sample.add("P001");
        sample.add("示例商品");
        sample.add("数码");
        sample.add("99.90");
        sample.add("100");
        sample.add("下架");
        sample.add("示例备注");
        rows.add(sample);
        ExcelUtil.exportXlsx(response, "商品导入模板", "商品模板", headers, rows);
    }

    private String getCell(List<String> row, int index) {
        return index < row.size() ? row.get(index) : null;
    }
}
