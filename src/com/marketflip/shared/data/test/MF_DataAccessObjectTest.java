package com.marketflip.shared.data.test;


import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.marketflip.shared.data.MF_DataAccessObject;

/**
 * Tests for DAO super class
 * @author David Walters
 * Last updated: 12/22/2015
 *
 */

public class MF_DataAccessObjectTest {
	
	private static MF_DataAccessObject DAO;

	@Before
	public void setUp() throws Exception {
		DAO = new MF_DataAccessObject("testing", "master");
	}
	
	@After
	public void tearDown() throws Exception {
		
		if (DAO.isOpen()){
			DAO.close();
		}
	}

	
	@Test
	public void ConnectionStatus_Void_ExpectOpen () throws SQLException {
		DAO = new MF_DataAccessObject("testing", "master");
		assertTrue (DAO.isOpen());
	}

	@Test
	public void getEnvironment_Testing_Testing () {
		assertTrue ("testing".equals(DAO.getEnvironment()));
	}
	
	@Test
	public void close_Void_ExpectFalse () throws SQLException {
		DAO.close();
		assertFalse(DAO.isOpen());
	}

}
