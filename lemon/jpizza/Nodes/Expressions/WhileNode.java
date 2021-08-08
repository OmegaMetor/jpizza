package lemon.jpizza.Nodes.Expressions;

import lemon.jpizza.Constants;
import lemon.jpizza.Nodes.Node;

public class WhileNode extends Node {
    public Node condition_node;
    public Node body_node;
    public boolean retnull;
    public boolean conLast;

    public WhileNode(Node condition_node, Node body_node, boolean retnull, boolean conLast) {
        this.condition_node = condition_node;
        this.body_node = body_node;
        this.retnull = retnull;
        this.conLast = conLast;
        pos_start = condition_node.pos_start.copy(); pos_end = body_node.pos_end.copy();
        jptype = Constants.JPType.While;
    }

}
