
package org.onetwo.tcc.samples.product.service.impl;

import org.onetwo.common.db.spi.BaseEntityManager;
import org.onetwo.common.spring.copier.CopyUtils;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.dbm.core.internal.DbmCrudServiceImpl;
import org.onetwo.tcc.core.annotation.TCCService;
import org.onetwo.tcc.core.annotation.TCCTransactional;
import org.onetwo.tcc.samples.product.api.SkuApi.ReduceStockRequest;
import org.onetwo.tcc.samples.product.api.SkuApi.SkuVO;
import org.onetwo.tcc.samples.product.entity.SkuEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@TCCService
public class SkuServiceImpl extends DbmCrudServiceImpl<SkuEntity, Long>{

	@Autowired
    private BaseEntityManager baseEntityManager;
	
	public BaseEntityManager getBaseEntityManager() {
		return baseEntityManager;
	}
	
    public SkuEntity cleanAndCreateSku(SkuVO request) {
    	baseEntityManager.removeAll(SkuEntity.class);
    	SkuEntity sku = CopyUtils.copy(SkuEntity.class, request);
    	sku.setFrozenStockCount(0);
    	baseEntityManager.save(sku);
    	return sku;
    }
    
    
    @TCCTransactional(globalized=false, confirmMethod="confirmStock", cancelMethod="cancelStock")
    public void reduceStock(ReduceStockRequest request) {
    	SkuEntity sku = baseEntityManager.lockWrite(SkuEntity.class, request.getSkuId());
    	sku.setStockCount(sku.getStockCount()-request.getStockCount());
    	sku.setFrozenStockCount(sku.getFrozenStockCount()+request.getStockCount());
    	if (request.getSleepInSeconds()>0) {
    		LangUtils.await(request.getSleepInSeconds());
    	}
    	baseEntityManager.update(sku);
	}

    public void confirmStock(ReduceStockRequest request) {
    	SkuEntity sku = baseEntityManager.lockWrite(SkuEntity.class, request.getSkuId());
    	sku.setFrozenStockCount(sku.getFrozenStockCount()-request.getStockCount());
    	baseEntityManager.update(sku);
	}

    public void cancelStock(ReduceStockRequest request) {
    	SkuEntity sku = baseEntityManager.lockWrite(SkuEntity.class, request.getSkuId());
    	sku.setStockCount(sku.getStockCount()+request.getStockCount());
    	sku.setFrozenStockCount(sku.getFrozenStockCount()-request.getStockCount());
    	baseEntityManager.update(sku);
	}
}