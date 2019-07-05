package com.linyingjie.architecture.DistributedConsistencyHash;

public interface ConsistentHash {
    void addNode(String name);
    void delNode(String name);
    long getDataHashValue(byte[] data);
    VirtualNode findNodeByData(byte[] data);
    int getNodeCount();
}
