package com.jzone.module.demo.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jzone.common.pojo.PageResult;
import com.jzone.module.demo.product.pojo.dto.AddProductDTO;
import com.jzone.module.demo.product.pojo.dto.EditProductDTO;
import com.jzone.module.demo.product.pojo.dto.QueryProductDTO;
import com.jzone.module.demo.product.pojo.po.ProductPO;
import com.jzone.module.demo.product.pojo.vo.ProductVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService extends IService<ProductPO> {

    PageResult<ProductVO> pageProduct(QueryProductDTO dto);

    ProductVO getProductById(Long id);

    void addProduct(AddProductDTO dto);

    void editProduct(EditProductDTO dto);

    void deleteProduct(Long id);

    void exportProduct(QueryProductDTO dto, HttpServletResponse response);

    void importProduct(MultipartFile file);

    void downloadTemplate(HttpServletResponse response);
}
