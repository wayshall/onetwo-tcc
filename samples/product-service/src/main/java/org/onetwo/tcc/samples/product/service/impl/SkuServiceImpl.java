
package org.onetwo.tcc.samples.product.service.impl;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.tcc.core.annotation.TCCTransactional;
import org.onetwo.tcc.samples.api.SkuApi.ReduceStockRequest;
import org.onetwo.tcc.samples.product.entity.SkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SkuServiceImpl {

    @Autowired
    private BaseEntityManager baseEntityManager;
    
    @TCCTransactional(globalized=false, confirmMethod="confirmStock", cancelMethod="cancelStock")
    public void reduceStock(ReduceStockRequest request) {
    	SkuEntity sku = baseEntityManager.load(SkuEntity.class, request.getSkuId());
    	sku.setFrozenStockCount(sku.getFrozenStockCount()+request.getStockCount());
    	baseEntityManager.update(sku);
	}

    public void confirmStock(ReduceStockRequest request) {
    	SkuEntity sku = baseEntityManager.load(SkuEntity.class, request.getSkuId());
    	sku.setStockCount(sku.getStockCount()-request.getStockCount());
    	sku.setFrozenStockCount(sku.getFrozenStockCount()-request.getStockCount());
    	baseEntityManager.update(sku);
	}

    public void cancelStock(ReduceStockRequest request) {
    	SkuEntity sku = baseEntityManager.load(SkuEntity.class, request.getSkuId());
    	sku.setFrozenStockCount(sku.getFrozenStockCount()-request.getStockCount());
    	baseEntityManager.update(sku);
	}
}