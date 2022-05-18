package traversal.interpreterUtils;

import traversal.Interpreter;

import java.util.*;

public interface LoxCallable {
    int arity();

    Object call(Interpreter interpreter, List<Object> arguments);
}