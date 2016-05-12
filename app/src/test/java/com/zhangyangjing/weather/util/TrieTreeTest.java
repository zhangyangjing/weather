package com.zhangyangjing.weather.util;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Set;

/**
 * Created by zhangyangjing on 16/5/11.
 */
public class TrieTreeTest {
    private static final String DATA_PATH = "src/main/assets/city.data";

    private static TrieTree<String> mTrieTree;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        mTrieTree = new TrieTree();
    }

    @Test
    public void testAdd() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(DATA_PATH));

        int lineCnt = 0;
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("#"))
                continue;

            String[] segs = line.split(" ");
            String id = segs[0];
            String abbr = segs[1];
            String county = segs[2];
            String ccounty = segs[3];
            String city = segs[4];
            String province = segs[5];

            lineCnt++;
            mTrieTree.add(ccounty, ccounty);
            for (String keyword : abbr.split(",")) {
                lineCnt++;
                mTrieTree.add(keyword, ccounty);
            }
            for (String keyword : county.split(",")) {
                lineCnt++;
                mTrieTree.add(keyword, ccounty);
            }
        }

        System.out.println("add " + lineCnt + " test items");
    }

    @Test
    public void testGet() throws Exception {
        String[] testCases = {"jn", "nn", "cy", "hk", "zy", "zz", "wenshang", "洛阳"};
        for (String testCase : testCases) {
            Set<String> result = mTrieTree.get(testCase);
            Assert.assertFalse(0 == result.size());
            System.out.println("case " + testCase + " return " + result);
        }
    }

    @Test
    public void testDumpGraphviz() throws Exception {
        System.out.println(mTrieTree.dumpGraphviz());
    }
}
