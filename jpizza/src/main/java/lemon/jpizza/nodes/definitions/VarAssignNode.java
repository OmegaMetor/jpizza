package lemon.jpizza.nodes.definitions;

import lemon.jpizza.Constants;
import lemon.jpizza.contextuals.Context;
import lemon.jpizza.errors.RTError;
import lemon.jpizza.generators.Interpreter;
import lemon.jpizza.nodes.Node;
import lemon.jpizza.objects.Obj;
import lemon.jpizza.results.RTResult;
import lemon.jpizza.Token;

public class VarAssignNode extends Node {
    public Token var_name_tok;
    public Node value_node;
    public boolean locked;
    public boolean defining;
    public Integer min = null;
    public Integer max = null;
    public String type;

    public VarAssignNode setType(String type) {
        this.type = type;
        return this;
    }

    public VarAssignNode setDefining(boolean defining) {
        this.defining = defining;
        return this;
    }

    public VarAssignNode setRange(Integer min, Integer max) {
        this.max = max;
        this.min = min;
        return this;
    }

    public VarAssignNode(Token var_name_tok, Node value_node) {
        this.var_name_tok = var_name_tok;
        this.value_node = value_node;

        locked = false;
        defining = true;
        pos_start = var_name_tok.pos_start; pos_end = var_name_tok.pos_end;
        jptype = Constants.JPType.VarAssign;
    }

    public VarAssignNode(Token var_name_tok, Node value_node, boolean locked) {
        this.var_name_tok = var_name_tok;
        this.value_node = value_node;
        this.locked = locked;

        defining = true;
        pos_start = var_name_tok.pos_start; pos_end = var_name_tok.pos_end;
        jptype = Constants.JPType.VarAssign;
    }

    @SuppressWarnings("unused")
    public VarAssignNode(Token var_name_tok, Node value_node, boolean defining, int _x) {
        this.var_name_tok = var_name_tok;
        this.value_node = value_node;
        locked = false;

        this.defining = defining;
        pos_start = var_name_tok.pos_start; pos_end = var_name_tok.pos_end;
        jptype = Constants.JPType.VarAssign;
    }

    public RTResult visit(Interpreter inter, Context context) {
        RTResult res = new RTResult();

        String varName = (String) var_name_tok.value;
        Obj value = res.register(value_node.visit(inter, context));
        if (res.shouldReturn()) return res;

        RTError.ErrorDetails error;
        if (defining)
            error = context.symbolTable.define(varName, value, locked, type, min, max);
        else
            error = context.symbolTable.set(varName, value, locked);
        if (error != null) return res.failure(error.build(
                pos_start, pos_end,
                context
        ));

        return res.success(value);
    }

}
