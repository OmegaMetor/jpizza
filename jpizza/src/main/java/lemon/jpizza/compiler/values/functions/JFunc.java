package lemon.jpizza.compiler.values.functions;

import lemon.jpizza.compiler.Chunk;

import java.io.Serializable;
import java.util.List;

public class JFunc implements Serializable {
    public int arity;
    public final Chunk chunk;
    public String name;
    public List<String> returnType;

    // Only if the function is a method
    public boolean isPrivate;
    public boolean isStatic;
    public boolean isBin;
    public String owner;

    public int upvalueCount;
    public boolean async;

    public JFunc(String source) {
        arity = 0;
        name = "";
        chunk = new Chunk(source);

        upvalueCount = 0;
    }

    public String toString() {
        if (owner != null)
            return "<" + owner + "-method-" + name + ">";
        return "<function-" + name + ">";
    }

    public JFunc copy() {
        JFunc copy = new JFunc(chunk.source());

        copy.chunk.constants(chunk.constants().copy());
        copy.chunk.codeArray = chunk.codeArray;
        copy.chunk.positions = chunk.positions;

        copy.arity = arity;
        copy.name = name;
        copy.returnType = returnType;
        copy.isPrivate = isPrivate;
        copy.isStatic = isStatic;
        copy.isBin = isBin;
        copy.owner = owner;
        copy.upvalueCount = upvalueCount;
        copy.async = async;
        return copy;
    }

}
