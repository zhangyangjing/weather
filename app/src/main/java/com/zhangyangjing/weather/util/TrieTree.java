package com.zhangyangjing.weather.util;


import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Created by zhangyangjing on 16/5/11.
 */
public class TrieTree<T> {
    private TrieNode<T> mRootNode;

    public TrieTree() {
        mRootNode = new TrieNode<>();
    }

    public void add(String text, T data) {
        addRecursive(text, data, mRootNode);
    }

    public Set<T> get(String text) {
        Set<T> data = new HashSet<>();

        TrieNode<T> node = mRootNode;
        for (char c : text.toCharArray()) {
            node = node.getChildNode(c);
            if (null == node)
                return data;
        }

        getRecursive(data, node);
        return data;
    }

    public String dumpGraphviz() {
        StringBuilder nodeSb = new StringBuilder();
        StringBuilder connSb = new StringBuilder();
        dumpGraphvizRecursive(nodeSb, connSb, mRootNode);
        return String.format("digraph TrieTree {\n%s\n%s}", nodeSb.toString(), connSb.toString());
    }

    private void addRecursive(String text, T data, TrieNode<T> node) {
        if (null == text || 0 == text.length()) {
            node.addData(data);
        } else {
            char c = text.charAt(0);
            TrieNode<T> childNode = node.getChildNode(c);
            if (null == childNode) {
                childNode = new TrieNode<>();
                node.addChildNode(c, childNode);
            }
            addRecursive(text.substring(1), data, childNode);
        }
    }

    private void getRecursive(Set<T> data, TrieNode<T> node) {
        data.addAll(node.getData());
        for (TrieNode trieNode : node.getChildNodes())
            getRecursive(data, trieNode);
    }

    private void dumpGraphvizRecursive(StringBuilder nodeSb,
                                       StringBuilder connSb, TrieNode<T> node) {
        nodeSb.append(String.format("    %d [label=\"%s\"];\n", node.hashCode(), node.getData()));
        for (Map.Entry<Character, TrieNode<T>> child : node.getChildNodeWithSymbol()) {
            connSb.append(String.format("    %d -> %d [label=\"%s\"];\n",
                    node.hashCode(),
                    child.getValue().hashCode(),
                    child.getKey()));
            dumpGraphvizRecursive(nodeSb, connSb, child.getValue());
        }
    }

    private class TrieNode<T> {
        private Map<Character, TrieNode<T>> mChildNodes;
        private Set<T> mData;

        public TrieNode() {
            mChildNodes = new TreeMap<>();
            mData = new HashSet<>();
        }

        public void addChildNode(char c, TrieNode<T> node) {
            mChildNodes.put(c, node);
        }

        public TrieNode<T> getChildNode(char c) {
            return mChildNodes.get(c);
        }

        public Collection<TrieNode<T>> getChildNodes() {
            return mChildNodes.values();
        }

        Set<Map.Entry<Character, TrieNode<T>>> getChildNodeWithSymbol() {
            return mChildNodes.entrySet();
        }

        public void addData(T data) {
            mData.add(data);
        }

        public Set<T> getData() {
            return mData;
        }
    }
}
