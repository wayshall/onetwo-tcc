

package org.onetwo.tcc.samples.product.controller;

import javax.validation.Valid;

import org.onetwo.tcc.samples.api.SkuApi;
import org.onetwo.tcc.samples.product.service.impl.SkuServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkuController implements SkuApi {

    @Autowired
    private SkuServiceImpl skuService;

	@Override
	public void reduceStock(@Valid @RequestBody ReduceStockRequest request) {
		skuService.reduceStock(request);
	}

    
    
}