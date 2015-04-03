package test;

import static org.junit.Assert.*;
import node.SiteImpl;
import node.SiteItf;

import org.junit.Test;

/**
 * JUnits Test
 * @author florian Malapel & Antonin Noel
 */
public class SiteImplTest {

	@Test
	/**
	 * Test if the Id of an instance of a SiteItf is correct
	 * @throws Exception
	 */
	public void testId() throws Exception {
		SiteItf instance = (SiteItf) new SiteImpl(1);
		assertEquals(instance.getId(), 1);
	}

	@Test
	/**
	 * Test the link between a child and a father
	 * @throws Exception
	 */
	public void testLink() throws Exception {
		SiteItf father = (SiteItf) new SiteImpl(1);
		SiteItf child = (SiteItf) new SiteImpl(2);
		assertEquals(father.getId(), 1);
		assertEquals(child.getId(), 2);
		father.addChild(child);
		child.addAfather(father);
		assertEquals(father.getChildren(), 1);
		assertEquals(father.getFathers(), 0);
		assertEquals(child.getFathers(), 1);
		assertEquals(child.getChildren(), 0);
	}
	
	
}
