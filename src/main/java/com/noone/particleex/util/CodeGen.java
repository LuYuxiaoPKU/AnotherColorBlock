package com.noone.particleex.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class CodeGen {
   private static final Map<EnumToken, Integer> IOPTOOP = Maps.newHashMap();
   private static final Map<EnumToken, Integer> DOPTOOP = Maps.newHashMap();
   private static final Map<EnumToken, Integer> DIOPTOOP = Maps.newHashMap();
   private static final List<String> FIELDS = Lists.newArrayList();
   private static final Method[] METHODS = Math.class.getMethods();
   private final Expression[] block;
   private final ClassWriter cw = new ClassWriter(2);
   private MethodVisitor mv;
   private MethodVisitor simulationMv;
   private int simulationCount;
   private int maxLocal;
   private final Map<String, CodeGen.LocalVarInfo> localVars = new HashMap<>();

   public CodeGen(Expression[] block) {
      this.block = Arrays.copyOf(block, block.length);
   }

   public Class<?> codeGenBlock(String name) {
      this.cw.visit(52, 33, "com/noone/particleex/util/CodeGen$" + name, null, "java/lang/Object", null);
      this.cw.visitInnerClass("com/noone/particleex/util/CodeGen$" + name, "com/noone/particleex/util/CodeGen", name, 9);
      this.mv = this.cw.visitMethod(9, "invoke", "(Lcom/noone/particleex/util/ParticleStruct;)I", null, null);
      this.addLocalVar("this", -1);
      this.mv.visitCode();

      for(int i = 0; i < this.block.length - 1; ++i) {
         this.codeGenExp(this.block[i], 0);
      }

      this.codeGenReturn(this.block[this.block.length - 1]);
      this.mv.visitMaxs(0, 0);
      this.mv.visitEnd();
      this.cw.visitEnd();

      byte[] bytes = this.cw.toByteArray();
      try {
         MethodHandles.Lookup lookup = MethodHandles.lookup();

          return lookup.defineHiddenClass(
                 bytes,
                 true,
                 MethodHandles.Lookup.ClassOption.NESTMATE
         ).lookupClass();
      } catch (IllegalAccessException e) {
         throw new RuntimeException("Class definition failed", e);
      }

   }

   private int codeGenExp(Expression exp, int targetType) {
       return switch (exp) {
           case Expression.IntegerExp integerExp ->
                   this.codeGenTypeTransform(this.codeGenLoadInteger(integerExp.val), targetType);
           case Expression.FloatExp floatExp ->
                   this.codeGenTypeTransform(this.codeGenLoadFloat(floatExp.val), targetType);
           case Expression.IntegerMatrixExp integerMatrixExp ->
                   this.codeGenTypeTransform(this.codeGenLoadIntegerMatrix(integerMatrixExp.val), targetType);
           case Expression.FloatMatrixExp floatMatrixExp ->
                   this.codeGenTypeTransform(this.codeGenLoadFloatMatrix(floatMatrixExp.val), targetType);
           case Expression.MatrixExp matrixExp ->
                   this.codeGenTypeTransform(this.codeGenLoadMatrix(matrixExp.exps), targetType);
           case Expression.NameMatrixExp nameMatrixExp ->
                   this.codeGenTypeTransform(this.codeGenLoadMatrix(nameMatrixExp.names), targetType);
           case Expression.UnOpExp unopExp ->
                   this.codeGenTypeTransform(this.codeGenUnopExp(unopExp.op, unopExp.exp), targetType);
           case Expression.BinOpExp binopExp -> this.codeGenTypeTransform(this.codeGenBinopExp(binopExp), targetType);
           case Expression.NameExp nameExp -> this.codeGenTypeTransform(this.codeGenNameExp(nameExp.name), targetType);
           case Expression.FunctionCallExp functionCallExp ->
                   this.codeGenTypeTransform(this.codeGenFunctionCallExp(functionCallExp.name, functionCallExp.args), targetType);
           case null, default ->
                   exp instanceof Expression.AssignExp ? this.codeGenTypeTransform(this.codeGenAssignExp(((Expression.AssignExp) exp).varList, ((Expression.AssignExp) exp).expList, targetType != 0), targetType) : 0;
       };
   }

   private void codeGenReturn(Expression exp) {
      this.codeGenExp(exp, 10);
      this.mv.visitInsn(172);
   }

   private int codeGenLoadInteger(int n) {
      if (n >= -1 && n < 6) {
         this.mv.visitInsn(3 + n);
      } else if (n >= -128 && n < 128) {
         this.mv.visitIntInsn(16, n);
      } else if (n >= -32768 && n < 32768) {
         this.mv.visitIntInsn(17, n);
      } else {
         this.mv.visitLdcInsn(n);
      }

      return 10;
   }

   private int codeGenLoadFloat(double n) {
      if (n == 0.0D) {
         this.mv.visitInsn(14);
      } else if (n == 1.0D) {
         this.mv.visitInsn(15);
      } else {
         this.mv.visitLdcInsn(n);
      }

      return 7;
   }

   private int codeGenLoadIntegerMatrix(int[][] val) {
      this.codeGenLoadInteger(val.length);
      this.mv.visitTypeInsn(189, "[I");

      for(int i = 0; i < val.length; ++i) {
         this.mv.visitInsn(89);
         this.codeGenLoadInteger(i);
         this.codeGenLoadInteger(val[i].length);
         this.mv.visitIntInsn(188, 10);

         for(int j = 0; j < val[i].length; ++j) {
            this.mv.visitInsn(89);
            this.codeGenLoadInteger(j);
            this.codeGenLoadInteger(val[i][j]);
            this.mv.visitInsn(79);
         }

         this.mv.visitInsn(83);
      }

      return 12;
   }

   private int codeGenLoadFloatMatrix(double[][] val) {
      this.codeGenLoadInteger(val.length);
      this.mv.visitTypeInsn(189, "[D");

      for(int i = 0; i < val.length; ++i) {
         this.mv.visitInsn(89);
         this.codeGenLoadInteger(i);
         this.codeGenLoadInteger(val[i].length);
         this.mv.visitIntInsn(188, 7);

         for(int j = 0; j < val[i].length; ++j) {
            this.mv.visitInsn(89);
            this.codeGenLoadInteger(j);
            this.codeGenLoadFloat(val[i][j]);
            this.mv.visitInsn(82);
         }

         this.mv.visitInsn(83);
      }

      return 13;
   }

   private int codeGenLoadMatrix(Expression[][] exps) {
      this.codeGenLoadInteger(exps.length);
      this.mv.visitTypeInsn(189, "[D");

      for(int i = 0; i < exps.length; ++i) {
         this.mv.visitInsn(89);
         this.codeGenLoadInteger(i);
         this.codeGenLoadInteger(exps[i].length);
         this.mv.visitIntInsn(188, 7);

         for(int j = 0; j < exps[i].length; ++j) {
            this.mv.visitInsn(89);
            this.codeGenLoadInteger(j);
            this.codeGenExp(exps[i][j], 7);
            this.mv.visitInsn(82);
         }

         this.mv.visitInsn(83);
      }

      return 13;
   }

   private int codeGenUnopExp(EnumToken op, Expression exp) {
      switch(op) {
      case NEG:
          return switch (this.codeGenExp(exp, -1)) {
              case 7 -> {
                  this.mv.visitInsn(119);
                  yield 7;
              }
              default -> throw new RuntimeException("bad type");
              case 10 -> {
                  this.mv.visitInsn(116);
                  yield 10;
              }
              case 12 -> {
                  this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "matNeg", "([[I)[[I", false);
                  yield 12;
              }
              case 13 -> {
                  this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "matNeg", "([[D)[[D", false);
                  yield 13;
              }
          };
      case NOT:
         switch(this.codeGenExp(exp, -1)) {
         case 7:
            this.mv.visitInsn(14);
            this.mv.visitInsn(151);
            Label dJumpLabel = new Label();
            this.mv.visitJumpInsn(153, dJumpLabel);
            this.mv.visitInsn(14);
            Label dEndLabel = new Label();
            this.mv.visitJumpInsn(167, dEndLabel);
            this.mv.visitLabel(dJumpLabel);
            this.mv.visitInsn(15);
            this.mv.visitLabel(dEndLabel);
            return 7;
         case 10:
            Label iJumpLabel = new Label();
            this.mv.visitJumpInsn(153, iJumpLabel);
            this.mv.visitInsn(3);
            Label iEndLabel = new Label();
            this.mv.visitJumpInsn(167, iEndLabel);
            this.mv.visitLabel(iJumpLabel);
            this.mv.visitInsn(4);
            this.mv.visitLabel(iEndLabel);
            return 10;
         default:
            throw new RuntimeException("bad type");
         }
      default:
         throw new RuntimeException("bad operator: " + op);
      }
   }

   private int codeGenBinopExp(Expression.BinOpExp exp) {
      switch(exp.op) {
      case AND:
         this.codeGenExp(exp.lExp, 10);
         Label aJumpLabel = new Label();
         this.mv.visitJumpInsn(153, aJumpLabel);
         this.codeGenExp(exp.rExp, 10);
         this.mv.visitJumpInsn(153, aJumpLabel);
         this.mv.visitInsn(4);
         Label aEndLabel = new Label();
         this.mv.visitJumpInsn(167, aEndLabel);
         this.mv.visitLabel(aJumpLabel);
         this.mv.visitInsn(3);
         this.mv.visitLabel(aEndLabel);
         return 10;
      case OR:
         this.codeGenExp(exp.lExp, 10);
         Label oJumpLabel = new Label();
         this.mv.visitJumpInsn(154, oJumpLabel);
         this.codeGenExp(exp.rExp, 10);
         this.mv.visitJumpInsn(154, oJumpLabel);
         this.mv.visitInsn(3);
         Label oEndLabel = new Label();
         this.mv.visitJumpInsn(167, oEndLabel);
         this.mv.visitLabel(oJumpLabel);
         this.mv.visitInsn(4);
         this.mv.visitLabel(oEndLabel);
         return 10;
      default:
         if (exp.returnType == -1) {
            this.startSimulation();
            exp.returnType = this.upwardType(this.codeGenExp(exp.lExp, -1), this.codeGenExp(exp.rExp, -1));
            this.stopSimulation();
         }

         Label logicJumpLabel;
         Label logicEndLabel;
         switch(exp.returnType) {
         case 7:
            this.codeGenExp(exp.lExp, 7);
            this.codeGenExp(exp.rExp, 7);
            switch(exp.op) {
            case ADD:
               this.mv.visitInsn(99);
               return 7;
            case SUB:
               this.mv.visitInsn(103);
               return 7;
            case MUL:
               this.mv.visitInsn(107);
               return 7;
            case DIV:
               this.mv.visitInsn(111);
               return 7;
            case MOD:
               this.mv.visitInsn(115);
               return 7;
            case POW:
               this.mv.visitMethodInsn(184, "java/lang/Math", "pow", "(DD)D", false);
               return 7;
            case LT:
            case LE:
            case GT:
            case GE:
            case EQ:
            case NEQ:
               this.mv.visitInsn(DOPTOOP.get(exp.op));
               logicJumpLabel = new Label();
               this.mv.visitJumpInsn(DIOPTOOP.get(exp.op), logicJumpLabel);
               this.mv.visitInsn(4);
               logicEndLabel = new Label();
               this.mv.visitJumpInsn(167, logicEndLabel);
               this.mv.visitLabel(logicJumpLabel);
               this.mv.visitInsn(3);
               this.mv.visitLabel(logicEndLabel);
               return 10;
            default:
               throw new RuntimeException("bad operator: " + exp.op.token);
            }
         case 10:
            this.codeGenExp(exp.lExp, 10);
            this.codeGenExp(exp.rExp, 10);
            switch(exp.op) {
            case ADD:
               this.mv.visitInsn(96);
               return 10;
            case SUB:
               this.mv.visitInsn(100);
               return 10;
            case MUL:
               this.mv.visitInsn(104);
               return 10;
            case DIV:
               this.mv.visitInsn(108);
               return 10;
            case MOD:
               this.mv.visitInsn(112);
               return 10;
            case POW:
               this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "pow", "(II)D", false);
               return 7;
            case LT:
            case LE:
            case GT:
            case GE:
            case EQ:
            case NEQ:
               logicJumpLabel = new Label();
               this.mv.visitJumpInsn(IOPTOOP.get(exp.op), logicJumpLabel);
               this.mv.visitInsn(4);
               logicEndLabel = new Label();
               this.mv.visitJumpInsn(167, logicEndLabel);
               this.mv.visitLabel(logicJumpLabel);
               this.mv.visitInsn(3);
               this.mv.visitLabel(logicEndLabel);
               return 10;
            default:
               throw new RuntimeException("bad operator: " + exp.op.token);
            }
         default:
            int ltype = this.codeGenExp(exp.lExp, -1);
            int rtype = this.codeGenExp(exp.rExp, -1);
            int returntype = 0;
            String returnName = null;
            String arg1Name;
            switch(ltype) {
            case 12:
               arg1Name = "[[I";
               break;
            case 13:
               arg1Name = "[[D";
               returnName = "[[D";
               returntype = 13;
               break;
            default:
               throw new RuntimeException("the number must appear on the right side of the matrix");
            }

            String arg2Name;
            switch(rtype) {
            case 7:
               arg2Name = "D";
               returnName = "[[D";
               returntype = 13;
               break;
            case 8:
            case 9:
            case 11:
            default:
               throw new RuntimeException("bad type: " + rtype);
            case 10:
               arg2Name = "I";
               break;
            case 12:
               arg2Name = "[[I";
               break;
            case 13:
               arg2Name = "[[D";
               returnName = "[[D";
               returntype = 13;
            }

            if (returnName == null) {
               returnName = "[[I";
               returntype = 12;
            }

            String functionName = switch (exp.op) {
                case ADD -> "matAdd";
                case SUB -> "matSub";
                case MUL -> "matMul";
                case DIV -> "matDiv";
                case MOD -> "matMod";
                case POW -> "matPow";
                default -> throw new RuntimeException("bad operator: " + exp.op.token);
            };

             if ((!functionName.equals("matDiv") && !functionName.equals("matMod") || !arg2Name.startsWith("[[")) && (!functionName.equals("matPow") || arg2Name.equals("I"))) {
               this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", functionName, "(" + arg1Name + arg2Name + ")" + returnName, false);
               return returntype;
            } else {
               throw new RuntimeException("bad operator: " + exp.op.token);
            }
         }
      }
   }

   private int codeGenNameExp(String name) {
      if (FIELDS.contains(name)) {
         this.mv.visitVarInsn(25, 0);
         this.mv.visitFieldInsn(180, "com/noone/particleex/util/ParticleStruct", name, "D");
         return 7;
      } else if (this.localVars.containsKey(name)) {
         CodeGen.LocalVarInfo info = this.localVars.get(name);
          return switch (info.type) {
              case 7 -> {
                  this.mv.visitVarInsn(24, info.index);
                  yield 7;
              }
              default -> throw new RuntimeException("bad type: " + info.type);
              case 10 -> {
                  this.mv.visitVarInsn(21, info.index);
                  yield 10;
              }
              case 12 -> {
                  this.mv.visitVarInsn(25, info.index);
                  yield 12;
              }
              case 13 -> {
                  this.mv.visitVarInsn(25, info.index);
                  yield 13;
              }
          };
      } else {
         throw new RuntimeException("undefine var: " + name);
      }
   }

   private int codeGenFunctionCallExp(String name, Expression[] args) {
      List<Method> methods = new ArrayList<>();
      Method[] var4 = METHODS;
      int maxSimilarityIndex = var4.length;

      int i;
      Method method;
      for(i = 0; i < maxSimilarityIndex; ++i) {
         method = var4[i];
         if (method.getName().equals(name) && method.getParameterCount() == args.length) {
            methods.add(method);
         }
      }

      if (methods.isEmpty()) {
         throw new RuntimeException("function not found: " + name);
      } else {
         int maxSimilarity;
         for(maxSimilarity = 0; maxSimilarity < args.length; ++maxSimilarity) {
            if (args[maxSimilarity].returnType == -1) {
               this.startSimulation();
               args[maxSimilarity].returnType = this.codeGenExp(args[maxSimilarity], -1);
               this.stopSimulation();
            }
         }

         maxSimilarity = 0;
         maxSimilarityIndex = -1;
         int j;
         if (args.length == 0) {
            maxSimilarityIndex = 0;
         } else {
            label119:
            for(i = 0; i < methods.size(); ++i) {
               method = methods.get(i);
               Class<?>[] parameterTypes = method.getParameterTypes();
               int similarity = 0;

               for(j = 0; j < args.length; ++j) {
                  switch(args[j].returnType) {
                  case 7:
                     if (parameterTypes[j] == Integer.TYPE) {
                        similarity += 4;
                     } else {
                        if (parameterTypes[j] != Double.TYPE) {
                           continue label119;
                        }

                        similarity += 6;
                     }
                     break;
                  case 8:
                  case 9:
                  case 11:
                  default:
                     throw new RuntimeException("bad type: " + args[j].returnType);
                  case 10:
                     if (parameterTypes[j] == Integer.TYPE) {
                        similarity += 6;
                     } else {
                        if (parameterTypes[j] != Double.TYPE) {
                           continue label119;
                        }

                        similarity += 5;
                     }
                     break;
                  case 12:
                     if (parameterTypes[j] == Integer.TYPE) {
                        similarity += 3;
                     } else {
                        if (parameterTypes[j] != Double.TYPE) {
                           continue label119;
                        }

                        similarity += 2;
                     }
                     break;
                  case 13:
                     if (parameterTypes[j] == Integer.TYPE) {
                        ++similarity;
                     } else {
                        if (parameterTypes[j] != Double.TYPE) {
                           continue label119;
                        }

                        similarity += 3;
                     }
                  }
               }

               if (similarity > maxSimilarity) {
                  maxSimilarity = similarity;
                  maxSimilarityIndex = i;
               }
            }
         }

         if (maxSimilarityIndex == -1) {
            throw new RuntimeException("function not found: " + name);
         } else {
            method = methods.get(maxSimilarityIndex);
            Class<?>[] parameterTypes = method.getParameterTypes();
            Class<?> returnType = method.getReturnType();
            StringBuilder sb = new StringBuilder();
            sb.append("(");

            for(j = 0; j < args.length; ++j) {
               this.codeGenExp(args[j], parameterTypes[j] == Integer.TYPE ? 10 : 7);
               sb.append(parameterTypes[j] == Integer.TYPE ? "I" : "D");
            }

            sb.append(")");
            if (returnType == Long.TYPE) {
               sb.append("J");
               this.mv.visitMethodInsn(184, "java/lang/Math", name, sb.toString(), false);
               this.mv.visitInsn(136);
               return 10;
            } else {
               sb.append(returnType == Integer.TYPE ? "I" : "D");
               this.mv.visitMethodInsn(184, "java/lang/Math", name, sb.toString(), false);
               return returnType == Integer.TYPE ? 10 : 7;
            }
         }
      }
   }

   private int codeGenAssignExp(Expression[] varList, Expression[] expList, boolean needReturn) {
      int[] types = new int[expList.length];

      int i;
      for(i = 0; i < expList.length; ++i) {
         if (varList[i] instanceof Expression.NameExp && FIELDS.contains(((Expression.NameExp)varList[i]).name)) {
            this.mv.visitVarInsn(25, 0);
         }

         types[i] = this.codeGenExp(expList[i], -1);
      }

      if (needReturn) {
         this.mv.visitInsn(types[expList.length - 1] == 7 ? 92 : 89);
         this.codeGenStore("__RETURN", types[expList.length - 1]);
      }

      for(i = varList.length - 1; i >= 0; --i) {
         if (varList[i] instanceof Expression.NameExp) {
            this.codeGenStore(((Expression.NameExp)varList[i]).name, types[i]);
         } else if (varList[i] instanceof Expression.NameMatrixExp) {
            if (types[i] != 12 && types[i] != 13) {
               throw new RuntimeException("can't deconstruction number: " + types[i]);
            }

            Expression.NameExp[][] names = ((Expression.NameMatrixExp)varList[i]).names;

            for(int j = 0; j < names.length; ++j) {
               if (j < names.length - 1) {
                  this.mv.visitInsn(89);
               }

               this.codeGenLoadInteger(j);
               this.mv.visitInsn(50);

               for(int k = 0; k < names[j].length; ++k) {
                  if (k < names[j].length - 1) {
                     this.mv.visitInsn(89);
                  }

                  this.codeGenLoadInteger(k);
                  this.mv.visitInsn(types[i] == 12 ? 46 : 49);
                  this.codeGenStore("__TEMP", types[i] == 12 ? 10 : 7);
                  this.mv.visitVarInsn(25, 0);
                  this.codeGenNameExp("__TEMP");
                  this.codeGenStore(names[j][k].name, types[i] == 12 ? 10 : 7);
               }
            }
         }
      }

      if (needReturn) {
         this.codeGenNameExp("__RETURN");
      }

      return needReturn ? types[expList.length - 1] : 0;
   }

   private int codeGenTypeTransform(int sourceType, int targetType) {
      if (targetType == -1) {
         return sourceType;
      } else if (sourceType == targetType) {
         return targetType;
      } else if (targetType == 0) {
          if (sourceType == 7) {
              this.mv.visitInsn(88);
          } else {
              this.mv.visitInsn(87);
          }

         return targetType;
      } else {
         switch(sourceType) {
         case 7:
            switch(targetType) {
            case 10:
               this.mv.visitInsn(142);
               return targetType;
            case 11:
            default:
               throw new RuntimeException("bad type: " + targetType);
            case 12:
               this.mv.visitInsn(142);
               this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "toMat", "(I)[[I", false);
               return targetType;
            case 13:
               this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "toMat", "(D)[[D", false);
               return targetType;
            }
         case 8:
         case 9:
         case 11:
         default:
            throw new RuntimeException("bad type: " + targetType);
         case 10:
            switch(targetType) {
            case 7:
               this.mv.visitInsn(135);
               return targetType;
            case 12:
               this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "toMat", "(I)[[I", false);
               return targetType;
            case 13:
               this.mv.visitInsn(135);
               this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "toMat", "(D)[[D", false);
               return targetType;
            default:
               throw new RuntimeException("bad type: " + targetType);
            }
         case 12:
            switch(targetType) {
            case 7:
               this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "toNumber", "([[I)I", false);
               this.mv.visitInsn(135);
               return targetType;
            case 10:
               this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "toNumber", "([[I)I", false);
               return targetType;
            case 13:
                this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "matToMat", "([[I)[[D", false);
                return targetType;
            default:
               throw new RuntimeException("bad type: " + targetType);
            }
         case 13:
            switch(targetType) {
            case 7:
               this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "toNumber", "([[D)D", false);
               return targetType;
            case 10:
               this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "toNumber", "([[D)D", false);
               this.mv.visitInsn(142);
               return targetType;
            case 12:
                this.mv.visitMethodInsn(184, "com/noone/particleex/util/MatrixUtil", "matToMat", "([[D)[[I", false);
                return targetType;
            default:
               throw new RuntimeException("bad type: " + targetType);
            }
         }
      }
   }

   private void codeGenStore(String name, int targetType) {
      if (FIELDS.contains(name)) {
         this.codeGenTypeTransform(targetType, 7);
         this.mv.visitFieldInsn(181, "com/noone/particleex/util/ParticleStruct", name, "D");
      } else {
         if (!this.localVars.containsKey(name)) {
            this.addLocalVar(name, targetType);
         }

         CodeGen.LocalVarInfo info = this.localVars.get(name);
         switch(info.type) {
         case 7:
            this.codeGenTypeTransform(targetType, 7);
            this.mv.visitVarInsn(57, info.index);
            return;
         case 8:
         case 9:
         case 11:
         default:
            throw new RuntimeException("bad type: " + info.type);
         case 10:
            this.codeGenTypeTransform(targetType, 10);
            this.mv.visitVarInsn(54, info.index);
            return;
         case 12:
            this.codeGenTypeTransform(targetType, 12);
            this.mv.visitVarInsn(58, info.index);
            return;
         case 13:
            this.codeGenTypeTransform(targetType, 13);
            this.mv.visitVarInsn(58, info.index);
         }
      }
   }

   private int upwardType(int ltype, int rtype) {
      if (Math.max(ltype, rtype) <= 0) {
         throw new RuntimeException("bad type");
      } else if (ltype == 10 && rtype == 10) {
         return 10;
      } else {
         return (ltype != 10 || rtype != 7) && (ltype != 7 || rtype != 10) && (ltype != 7 || rtype != 7) ? -1 : 7;
      }
   }

   private void addLocalVar(String name, int type) {
      int index = this.maxLocal;
      if (type == 7) {
         this.maxLocal += 2;
      } else {
         ++this.maxLocal;
      }

      this.localVars.put(name, new CodeGen.LocalVarInfo(type, index));
   }

   private void startSimulation() {
      if (this.simulationCount++ == 0) {
         this.simulationMv = this.mv;
         this.mv = new MethodVisitor(262144) {
         };
      }

   }

   private void stopSimulation() {
      if (--this.simulationCount == 0) {
         this.mv = this.simulationMv;
      }

   }

   static {
      IOPTOOP.put(EnumToken.LT, 162);
      IOPTOOP.put(EnumToken.LE, 163);
      IOPTOOP.put(EnumToken.GT, 164);
      IOPTOOP.put(EnumToken.GE, 161);
      IOPTOOP.put(EnumToken.EQ, 160);
      IOPTOOP.put(EnumToken.NEQ, 159);
      DOPTOOP.put(EnumToken.LT, 152);
      DOPTOOP.put(EnumToken.LE, 152);
      DOPTOOP.put(EnumToken.GT, 151);
      DOPTOOP.put(EnumToken.GE, 151);
      DOPTOOP.put(EnumToken.EQ, 151);
      DOPTOOP.put(EnumToken.NEQ, 151);
      DIOPTOOP.put(EnumToken.LT, 156);
      DIOPTOOP.put(EnumToken.LE, 157);
      DIOPTOOP.put(EnumToken.GT, 158);
      DIOPTOOP.put(EnumToken.GE, 155);
      DIOPTOOP.put(EnumToken.EQ, 154);
      DIOPTOOP.put(EnumToken.NEQ, 153);
      Field[] var0 = ParticleStruct.class.getFields();

       for (Field field : var0) {
           int modifiers = field.getModifiers();
           if (Modifier.isPublic(modifiers) && !Modifier.isStatic(modifiers)) {
               FIELDS.add(field.getName());
           }
       }

   }

   private record LocalVarInfo(int type, int index) {
   }
}
