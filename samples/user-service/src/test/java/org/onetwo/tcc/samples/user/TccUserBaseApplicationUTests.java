package org.onetwo.tcc.samples.user;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onetwo.tcc.samples.user.TccUserServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=TccUserServiceApplication.class)
@ActiveProfiles("dev")
public class TccUserBaseApplicationUTests {
	
	@Autowired
	protected ApplicationContext applicationContext;

	@BeforeClass
	public static void setupClass(){
	}
	@Test
	public void contextLoads() {
	}

}
