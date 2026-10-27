package com.jzone.module.demo.product.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jzone.common.pojo.PageResult;
import com.jzone.framework.result.ResultBean;
import com.jzone.module.demo.product.pojo.dto.AddProductDTO;
import com.jzone.module.demo.product.pojo.dto.EditProductDTO;
import com.jzone.module.demo.product.pojo.dto.QueryProductDTO;
import com.jzone.module.demo.product.pojo.vo.ProductVO;
import com.jzone.module.demo.product.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/demo/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/page")
    @SaCheckPermission("demo:product:query")
    public ResultBean<PageResult<ProductVO>> page(QueryProductDTO dto) {
        return ResultBean.success(productService.pageProduct(dto));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("demo:product:query")
    public ResultBean<ProductVO> getById(@PathVariable Long id) {
        return ResultBean.success(productService.getProductById(id));
    }

    @PostMapping
    @SaCheckPermission("demo:product:create")
    public ResultBean<Void> add(@RequestBody @Validated AddProductDTO dto) {
        productService.addProduct(dto);
        return ResultBean.success();
    }

    @PutMapping
    @SaCheckPermission("demo:product:update")
    public ResultBean<Void> edit(@RequestBody @Validated EditProductDTO dto) {
        productService.editProduct(dto);
        return ResultBean.success();
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("demo:product:delete")
    public ResultBean<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResultBean.success();
    }

    @GetMapping("/export")
    @SaCheckPermission("demo:product:export")
    public void export(QueryProductDTO dto, HttpServletResponse response) {
        productService.exportProduct(dto, response);
    }

    @PostMapping("/import")
    @SaCheckPermission("demo:product:import")
    public ResultBean<Void> importExcel(@RequestParam(value = "file", required = false) MultipartFile file) {
        productService.importProduct(file);
        return ResultBean.success();
    }

    @GetMapping("/template")
    public void template(HttpServletResponse response) {
        productService.downloadTemplate(response);
    }
}
