package traversal;

import parser.expr.*;
import parser.stmts.*;
import traversal.interpreterUtils.*;

import java.util.*;

public class Interpreter implements ExprVisitor<Object>, StmtVisitor<Void> {

    public final Environment globals = new Environment();
    private Environment environment = globals;


    public Interpreter() {
        globals.define("clock", new LoxCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter,
                               List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            error.printStackTrace();
        }
    }

    public void executeBlock(List<Stmt> statements,
                             Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } finally {
            this.environment = previous;
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    public void execute(Stmt stmt) {
        stmt.accept(this);
    }


    @Override
    public Object visitAssignExpr(Assign expr) {
        Object obj = evaluate(expr.value);
        this.environment.assign(expr.name, obj);
        return obj;
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        Object left = this.evaluate(expr.left);
        Object right = this.evaluate(expr.right);
        if (left instanceof Literal) {
            left = ((Literal) left).value;
        }
        if (right instanceof Literal) {
            right = ((Literal) right).value;
        }
        return switch (expr.operator.type) {
            case EQUAL_EQUAL -> (boolean) left == (boolean) right;
            case BANG_EQUAL -> (boolean) left != (boolean) right;
            case GREATER -> (double) left > (double) right;
            case GREATER_EQUAL -> (double) left >= (double) right;
            case LESS -> (double) left < (double) right;
            case LESS_EQUAL -> (double) left <= (double) right;
            case PLUS -> (double) left + (double) right;
            case MINUS -> (double) left - (double) right;
            case STAR -> (double) left * (double) right;
            case SLASH -> (double) left / (double) right;
            default -> null;
        };
    }

    @Override
    public Object visitCallExpr(Call expr) {
        Object obj = evaluate(expr.callee);
        LoxCallable function = (LoxCallable) obj;
        List<Object> FunctionList = new ArrayList<>(expr.arguments);
        return function.call(this, FunctionList);
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Logical expr) {
        Object right = this.evaluate(expr.right);
        Object left = this.evaluate(expr.left);
        return switch (expr.operator.type) {
            case OR -> (boolean) right || (boolean) left;
            case AND -> (boolean) right && (boolean) left;
            default -> null;
        };
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        Object right = this.evaluate(expr.right);
        if (expr.operator == null)
            return evaluate(expr.right);
        return switch (expr.operator.type) {
            case BANG -> !(boolean) right;
            case MINUS -> -(double) right;
            default -> null;
        };
    }

    @Override
    public Object visitVariableExpr(Variable expr) {
        return environment.get(expr.name);
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
        evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
        LoxFunction loxFunc = new LoxFunction(stmt, environment);
        environment.define(stmt.name.lexeme, loxFunc);
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
        if ((boolean) evaluate(stmt.condition)) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
        Object obj = evaluate(stmt.expression);
        System.out.println(obj);
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
        Object result = null;
        if (stmt.value != null) {
            result = evaluate(stmt.value);
        }
        throw new LoxReturn(result);
    }

    @Override
    public Void visitVarStmt(Var stmt) {
        this.environment.define(stmt.name.lexeme, evaluate(stmt.initializer));
        return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
        Object obj = evaluate(stmt.condition);
        while ((boolean) obj) {
            execute(stmt.body);
            obj = evaluate(stmt.condition);
        }
        return null;
    }
}