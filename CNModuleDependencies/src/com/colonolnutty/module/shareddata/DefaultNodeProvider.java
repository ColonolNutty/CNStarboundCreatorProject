package com.colonolnutty.module.shareddata;

/**
 * User: Jack's Computer
 * Date: 12/18/2017
 * Time: 3:30 PM
 */
public class DefaultNodeProvider implements IRequireNodeProvider {
    protected NodeProvider _nodeProvider;

    public DefaultNodeProvider() {
        _nodeProvider = new NodeProvider();
    }

    @Override
    public void setNodeProvider(NodeProvider nodeProvider) {
        _nodeProvider = nodeProvider;
    }
}
