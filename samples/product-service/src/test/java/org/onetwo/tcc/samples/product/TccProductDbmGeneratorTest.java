package org.onetwo.tcc.samples.product;

import javax.sql.DataSource;

import org.junit.Test;
import org.onetwo.common.db.generator.DbmGenerator;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wayshall
 * <br/>
 */
public class TccProductDbmGeneratorTest extends TccProductBaseApplicationUTests {
	
	@Autowired
	private DataSource dataSource;
	
	@Test
	public void generateUser(){
		DbmGenerator.dataSource(dataSource)
					.javaBasePackage(this.getClass().getPackage().getName())//基础包名
					.stripTablePrefix("prd_")//生成的文件会去掉act_前缀
					.mavenProjectDir()
					.webadminGenerator("prd_sku")
						.generateEntity()
						.generateController(Object.class)
						.generateServiceImpl()
					.end()
					.build()
					.generate();
					;
	}


}
