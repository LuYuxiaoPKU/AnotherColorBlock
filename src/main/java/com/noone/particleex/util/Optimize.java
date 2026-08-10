package com.noone.particleex.util;

public class Optimize {
   public static Expression optimizeLogicalOr(Expression.BinOpExp exp) {
      if (trueOnly(exp.lExp)) {
         return exp.lExp;
      } else {
         return falseOnly(exp.lExp) ? exp.rExp : exp;
      }
   }

   public static Expression optimizeLogicalAnd(Expression.BinOpExp exp) {
      if (falseOnly(exp.lExp)) {
         return exp.lExp;
      } else {
         return trueOnly(exp.lExp) ? exp.rExp : exp;
      }
   }

   public static Expression optimizeArithmeticBinaryOp(Expression.BinOpExp exp) {
      if (exp.lExp instanceof Expression.IntegerExp && exp.rExp instanceof Expression.IntegerExp) {
         int r;
         int l = ((Expression.IntegerExp)exp.lExp).val;
         r = ((Expression.IntegerExp)exp.rExp).val;
          return switch (exp.op) {
              case ADD -> new Expression.IntegerExp(exp.line, l + r);
              case SUB -> new Expression.IntegerExp(exp.line, l - r);
              case MUL -> new Expression.IntegerExp(exp.line, l * r);
              case DIV -> new Expression.IntegerExp(exp.line, l / r);
              case MOD -> new Expression.IntegerExp(exp.line, l % r);
              case POW -> {
                  if (r >= 0) {
                      yield new Expression.IntegerExp(exp.line, pow(l, r));
                  }
                  yield new Expression.FloatExp(exp.line, Math.pow(l, r));
              }
              default -> exp;
          };
      } else if ((!(exp.lExp instanceof Expression.FloatExp) || !(exp.rExp instanceof Expression.FloatExp)) && (!(exp.lExp instanceof Expression.IntegerExp) || !(exp.rExp instanceof Expression.FloatExp)) && (!(exp.lExp instanceof Expression.FloatExp) || !(exp.rExp instanceof Expression.IntegerExp))) {
         if (exp.lExp instanceof Expression.IntegerMatrixExp) {
            int[][] lMat = ((Expression.IntegerMatrixExp)exp.lExp).val;
            if (exp.rExp instanceof Expression.IntegerExp) {
               int r = ((Expression.IntegerExp)exp.rExp).val;
               switch(exp.op) {
               case ADD:
                  return new Expression.IntegerMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matAdd(lMat, r));
               case SUB:
                  return new Expression.IntegerMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matSub(lMat, r));
               case MUL:
                  return new Expression.IntegerMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matMul(lMat, r));
               case DIV:
                  return new Expression.IntegerMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matDiv(lMat, r));
               case MOD:
                  return new Expression.IntegerMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matMod(lMat, r));
               case POW:
                  return new Expression.IntegerMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matPow(lMat, r));
               }
            } else if (exp.rExp instanceof Expression.FloatExp) {
               double r = ((Expression.FloatExp)exp.rExp).val;
               switch(exp.op) {
               case ADD:
                  return new Expression.FloatMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matAdd(lMat, r));
               case SUB:
                  return new Expression.FloatMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matSub(lMat, r));
               case MUL:
                  return new Expression.FloatMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matMul(lMat, r));
               case DIV:
                  return new Expression.FloatMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matDiv(lMat, r));
               case MOD:
                  return new Expression.FloatMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matMod(lMat, r));
               }
            } else if (exp.rExp instanceof Expression.IntegerMatrixExp) {
               int[][] rMat = ((Expression.IntegerMatrixExp)exp.rExp).val;
               switch(exp.op) {
               case ADD:
                  return new Expression.IntegerMatrixExp(exp.lExp.line, ((Expression.IntegerMatrixExp)exp.rExp).lastLine, MatrixUtil.matAdd(lMat, rMat));
               case SUB:
                  return new Expression.IntegerMatrixExp(exp.lExp.line, ((Expression.IntegerMatrixExp)exp.rExp).lastLine, MatrixUtil.matSub(lMat, rMat));
               case MUL:
                  return new Expression.IntegerMatrixExp(exp.lExp.line, ((Expression.IntegerMatrixExp)exp.rExp).lastLine, MatrixUtil.matMul(lMat, rMat));
               }
            } else if (exp.rExp instanceof Expression.FloatMatrixExp) {
               double[][] rMat = ((Expression.FloatMatrixExp)exp.rExp).val;
               switch(exp.op) {
               case ADD:
                  return new Expression.FloatMatrixExp(exp.lExp.line, ((Expression.FloatMatrixExp)exp.rExp).lastLine, MatrixUtil.matAdd(lMat, rMat));
               case SUB:
                  return new Expression.FloatMatrixExp(exp.lExp.line, ((Expression.FloatMatrixExp)exp.rExp).lastLine, MatrixUtil.matSub(lMat, rMat));
               case MUL:
                  return new Expression.FloatMatrixExp(exp.lExp.line, ((Expression.FloatMatrixExp)exp.rExp).lastLine, MatrixUtil.matMul(lMat, rMat));
               }
            }
         } else if (exp.lExp instanceof Expression.FloatMatrixExp) {
            double[][] lMat = ((Expression.FloatMatrixExp)exp.lExp).val;
            if (exp.op == EnumToken.POW && exp.rExp instanceof Expression.IntegerExp) {
               return new Expression.FloatMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matPow(lMat, ((Expression.IntegerExp)exp.rExp).val));
            }

            if (!(exp.rExp instanceof Expression.IntegerExp) && !(exp.rExp instanceof Expression.FloatExp)) {
               if (exp.rExp instanceof Expression.IntegerMatrixExp) {
                  int[][] rMat = ((Expression.IntegerMatrixExp)exp.rExp).val;
                  switch(exp.op) {
                  case ADD:
                     return new Expression.FloatMatrixExp(exp.lExp.line, ((Expression.IntegerMatrixExp)exp.rExp).lastLine, MatrixUtil.matAdd(lMat, rMat));
                  case SUB:
                     return new Expression.FloatMatrixExp(exp.lExp.line, ((Expression.IntegerMatrixExp)exp.rExp).lastLine, MatrixUtil.matSub(lMat, rMat));
                  case MUL:
                     return new Expression.FloatMatrixExp(exp.lExp.line, ((Expression.IntegerMatrixExp)exp.rExp).lastLine, MatrixUtil.matMul(lMat, rMat));
                  }
               } else if (exp.rExp instanceof Expression.FloatMatrixExp) {
                  double[][] rMat = ((Expression.FloatMatrixExp)exp.rExp).val;
                  switch(exp.op) {
                  case ADD:
                     return new Expression.FloatMatrixExp(exp.lExp.line, ((Expression.FloatMatrixExp)exp.rExp).lastLine, MatrixUtil.matAdd(lMat, rMat));
                  case SUB:
                     return new Expression.FloatMatrixExp(exp.lExp.line, ((Expression.FloatMatrixExp)exp.rExp).lastLine, MatrixUtil.matSub(lMat, rMat));
                  case MUL:
                     return new Expression.FloatMatrixExp(exp.lExp.line, ((Expression.FloatMatrixExp)exp.rExp).lastLine, MatrixUtil.matMul(lMat, rMat));
                  }
               }
            } else {
               double r = exp.rExp instanceof Expression.IntegerExp ? (double)((Expression.IntegerExp)exp.rExp).val : ((Expression.FloatExp)exp.rExp).val;
               switch(exp.op) {
               case ADD:
                  return new Expression.FloatMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matAdd(lMat, r));
               case SUB:
                  return new Expression.FloatMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matSub(lMat, r));
               case MUL:
                  return new Expression.FloatMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matMul(lMat, r));
               case DIV:
                  return new Expression.FloatMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matDiv(lMat, r));
               case MOD:
                  return new Expression.FloatMatrixExp(exp.lExp.line, exp.rExp.line, MatrixUtil.matMod(lMat, r));
               }
            }
         }

         return exp;
      } else {
         double l = exp.lExp instanceof Expression.IntegerExp ? (double)((Expression.IntegerExp)exp.lExp).val : ((Expression.FloatExp)exp.lExp).val;
         double r = exp.rExp instanceof Expression.IntegerExp ? (double)((Expression.IntegerExp)exp.rExp).val : ((Expression.FloatExp)exp.rExp).val;
          return switch (exp.op) {
              case ADD -> new Expression.FloatExp(exp.line, l + r);
              case SUB -> new Expression.FloatExp(exp.line, l - r);
              case MUL -> new Expression.FloatExp(exp.line, l * r);
              case DIV -> new Expression.FloatExp(exp.line, l / r);
              case MOD -> new Expression.FloatExp(exp.line, l % r);
              case POW -> new Expression.FloatExp(exp.line, Math.pow(l, r));
              default -> exp;
          };
      }
   }

   public static Expression optimizeUnaryOp(Expression.UnOpExp exp) {
      switch(exp.op) {
      case NEG:
         if (exp.exp instanceof Expression.IntegerExp) {
            return new Expression.IntegerExp(exp.line, -((Expression.IntegerExp)exp.exp).val);
         } else if (exp.exp instanceof Expression.FloatExp) {
            return new Expression.FloatExp(exp.line, -((Expression.FloatExp)exp.exp).val);
         } else if (exp.exp instanceof Expression.IntegerMatrixExp) {
            return new Expression.IntegerMatrixExp(exp.exp.line, ((Expression.IntegerMatrixExp)exp.exp).lastLine, MatrixUtil.matNeg(((Expression.IntegerMatrixExp)exp.exp).val));
         } else if (exp.exp instanceof Expression.FloatMatrixExp) {
            return new Expression.FloatMatrixExp(exp.exp.line, ((Expression.FloatMatrixExp)exp.exp).lastLine, MatrixUtil.matNeg(((Expression.FloatMatrixExp)exp.exp).val));
         }
      case NOT:
         if (exp.exp instanceof Expression.IntegerExp) {
            return new Expression.IntegerExp(exp.line, ((Expression.IntegerExp)exp.exp).val == 0 ? 1 : 0);
         } else if (exp.exp instanceof Expression.FloatExp) {
            return new Expression.FloatExp(exp.line, ((Expression.FloatExp)exp.exp).val == 0.0D ? 1.0D : 0.0D);
         }
      default:
         return exp;
      }
   }

   private static boolean trueOnly(Expression exp) {
      if (exp instanceof Expression.IntegerExp) {
         return ((Expression.IntegerExp)exp).val != 0;
      } else if (exp instanceof Expression.FloatExp) {
         return (int)((Expression.FloatExp)exp).val != 0;
      } else {
         return false;
      }
   }

   private static boolean falseOnly(Expression exp) {
      if (exp instanceof Expression.IntegerExp) {
         return ((Expression.IntegerExp)exp).val == 0;
      } else if (exp instanceof Expression.FloatExp) {
         return (int)((Expression.FloatExp)exp).val == 0;
      } else {
         return false;
      }
   }

   private static int pow(int l, int r) {
      int result = 1;

      for(int i = 0; i < r; ++i) {
         result *= l;
      }

      return result;
   }
}
