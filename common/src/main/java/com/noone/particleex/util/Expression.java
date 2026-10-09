package com.noone.particleex.util;

public class Expression {
   public final int line;
   public int returnType;

   protected Expression(int line, int returnType) {
      this.line = line;
      this.returnType = returnType;
   }

   public static class AssignExp extends Expression {
      public final Expression[] varList;
      public final Expression[] expList;

      public AssignExp(int line, Expression[] varList, Expression[] expList) {
         super(line, expList[expList.length - 1].returnType);
         this.varList = varList;
         this.expList = expList;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder();

         int i;
         for(i = 0; i < this.varList.length; ++i) {
            sb.append(this.varList[i].toString()).append(",");
         }

         sb.deleteCharAt(sb.length() - 1).append("=");

         for(i = 0; i < this.expList.length; ++i) {
            sb.append(this.expList[i].toString()).append(",");
         }

         return sb.deleteCharAt(sb.length() - 1).toString();
      }
   }

   public static class FunctionCallExp extends Expression {
      public final int lastLine;
      public final String name;
      public final Expression[] args;

      public FunctionCallExp(int line, int lastLine, String name, Expression[] args) {
         super(line, -1);
         this.lastLine = lastLine;
         this.name = name;
         this.args = args;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append(this.name).append("(");

          for (Expression arg : this.args) {
              sb.append(arg.toString()).append(",");
          }

         return sb.deleteCharAt(sb.length() - 1).append(")").toString();
      }
   }

   public static class BinOpExp extends Expression {
      public final EnumToken op;
      public final Expression lExp;
      public final Expression rExp;

      public BinOpExp(int line, EnumToken op, Expression lExp, Expression rExp) {
         super(line, -1);
         if (op == EnumToken.MINUS || op == EnumToken.NEG) {
            op = EnumToken.SUB;
         }

         this.op = op;
         this.lExp = lExp;
         this.rExp = rExp;
         if (op == EnumToken.AND || op == EnumToken.OR) {
            this.returnType = 10;
         }

         if (lExp.returnType != -1 && rExp.returnType != -1) {
            if (lExp.returnType == 10 && rExp.returnType == 10) {
               this.returnType = op == EnumToken.POW ? 7 : 10;
            } else if ((lExp.returnType != 10 || rExp.returnType != 7) && (lExp.returnType != 7 || rExp.returnType != 10) && (lExp.returnType != 7 || rExp.returnType != 7)) {
               if (lExp.returnType != 12 || rExp.returnType != 10 && rExp.returnType != 12) {
                  this.returnType = 13;
               } else {
                  this.returnType = 12;
               }
            } else {
               this.returnType = 7;
            }
         }

      }

      public String toString() {
         String var10000 = this.lExp.toString();
         return var10000 + this.op.token + this.rExp.toString();
      }
   }

   public static class UnOpExp extends Expression {
      public final EnumToken op;
      public final Expression exp;

      public UnOpExp(int line, EnumToken op, Expression exp) {
         super(line, exp.returnType);
         if (op == EnumToken.MINUS || op == EnumToken.SUB) {
            op = EnumToken.NEG;
         }

         this.op = op;
         this.exp = exp;
      }

      public String toString() {
         String var10000 = this.op.token;
         return var10000 + this.exp.toString();
      }
   }

   public static class NameExp extends Expression {
      public final String name;

      public NameExp(int line, String name) {
         super(line, -1);
         this.name = name;
      }

      public String toString() {
         return this.name;
      }
   }

   public static class NameMatrixExp extends Expression {
      public final int lastLine;
      public final Expression.NameExp[][] names;

      public NameMatrixExp(int line, int lastLine, Expression.NameExp[][] names) {
         super(line, 0);
         this.lastLine = lastLine;
         this.names = names;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append("(");

          for (NameExp[] name : this.names) {
              for (NameExp nameExp : name) {
                  sb.append(nameExp.name).append(",");
              }

              sb.deleteCharAt(sb.length() - 1).append(",,");
          }

         sb.delete(sb.length() - 2, sb.length()).append(")");
         return sb.toString();
      }
   }

   public static class MatrixExp extends Expression {
      public final int lastLine;
      public final Expression[][] exps;

      public MatrixExp(int line, int lastLine, Expression[][] exps) {
         super(line, 13);
         this.lastLine = lastLine;
         this.exps = exps;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append("(");

          for (Expression[] exp : this.exps) {
              for (Expression expression : exp) {
                  sb.append(expression.toString()).append(",");
              }

              sb.deleteCharAt(sb.length() - 1).append(",,");
          }

         sb.delete(sb.length() - 2, sb.length()).append(")");
         return sb.toString();
      }
   }

   public static class FloatMatrixExp extends Expression {
      public final int lastLine;
      public final double[][] val;

      public FloatMatrixExp(int line, int lastLine, double[][] val) {
         super(line, 13);
         this.lastLine = lastLine;
         this.val = val;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append("(");

          for (double[] doubles : this.val) {
              for (double aDouble : doubles) {
                  sb.append(aDouble).append(",");
              }

              sb.deleteCharAt(sb.length() - 1).append(",,");
          }

         sb.delete(sb.length() - 2, sb.length()).append(")");
         return sb.toString();
      }
   }

   public static class IntegerMatrixExp extends Expression {
      public final int lastLine;
      public final int[][] val;

      public IntegerMatrixExp(int line, int lastLine, int[][] val) {
         super(line, 12);
         this.lastLine = lastLine;
         this.val = val;
      }

      public String toString() {
         StringBuilder sb = new StringBuilder();
         sb.append("(");

          for (int[] intArray : this.val) {
              for (int anInt : intArray) {
                  sb.append(anInt).append(",");
              }

              sb.deleteCharAt(sb.length() - 1).append(",,");
          }

         sb.delete(sb.length() - 2, sb.length()).append(")");
         return sb.toString();
      }
   }

   public static class FloatExp extends Expression {
      public final double val;

      public FloatExp(int line, double val) {
         super(line, 7);
         this.val = val;
      }

      public String toString() {
         return String.valueOf(this.val);
      }
   }

   public static class IntegerExp extends Expression {
      public final int val;

      public IntegerExp(int line, int val) {
         super(line, 10);
         this.val = val;
      }

      public String toString() {
         return String.valueOf(this.val);
      }
   }
}
