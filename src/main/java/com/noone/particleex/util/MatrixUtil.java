package com.noone.particleex.util;

public class MatrixUtil {
   public static int[][] matAdd(int[][] lMat, int r) {
      int[][] result = new int[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] + r;
         }
      }

      return result;
   }

   public static double[][] matAdd(int[][] lMat, double r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = (double)lMat[i][j] + r;
         }
      }

      return result;
   }

   public static double[][] matAdd(double[][] lMat, int r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] + (double)r;
         }
      }

      return result;
   }

   public static double[][] matAdd(double[][] lMat, double r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] + r;
         }
      }

      return result;
   }

   public static int[][] matAdd(int[][] lMat, int[][] rMat) {
      int[][] result = new int[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] + rMat[i][j];
         }
      }

      return result;
   }

   public static double[][] matAdd(int[][] lMat, double[][] rMat) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = (double)lMat[i][j] + rMat[i][j];
         }
      }

      return result;
   }

   public static double[][] matAdd(double[][] lMat, int[][] rMat) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] + (double)rMat[i][j];
         }
      }

      return result;
   }

   public static double[][] matAdd(double[][] lMat, double[][] rMat) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] + rMat[i][j];
         }
      }

      return result;
   }

   public static int[][] matSub(int[][] lMat, int r) {
      int[][] result = new int[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] - r;
         }
      }

      return result;
   }

   public static double[][] matSub(int[][] lMat, double r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = (double)lMat[i][j] - r;
         }
      }

      return result;
   }

   public static double[][] matSub(double[][] lMat, int r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] - (double)r;
         }
      }

      return result;
   }

   public static double[][] matSub(double[][] lMat, double r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] - r;
         }
      }

      return result;
   }

   public static int[][] matSub(int[][] lMat, int[][] rMat) {
      int[][] result = new int[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] - rMat[i][j];
         }
      }

      return result;
   }

   public static double[][] matSub(int[][] lMat, double[][] rMat) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = (double)lMat[i][j] - rMat[i][j];
         }
      }

      return result;
   }

   public static double[][] matSub(double[][] lMat, int[][] rMat) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] - (double)rMat[i][j];
         }
      }

      return result;
   }

   public static double[][] matSub(double[][] lMat, double[][] rMat) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] - rMat[i][j];
         }
      }

      return result;
   }

   public static int[][] matMul(int[][] lMat, int r) {
      int[][] result = new int[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] * r;
         }
      }

      return result;
   }

   public static double[][] matMul(int[][] lMat, double r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = (double)lMat[i][j] * r;
         }
      }

      return result;
   }

   public static double[][] matMul(double[][] lMat, int r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] * (double)r;
         }
      }

      return result;
   }

   public static double[][] matMul(double[][] lMat, double r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] * r;
         }
      }

      return result;
   }

   public static int[][] matMul(int[][] lMat, int[][] rMat) {
      int[][] result = new int[lMat.length][rMat[0].length];

      for(int i = 0; i < result.length; ++i) {
         for(int j = 0; j < result[i].length; ++j) {
            for(int k = 0; k < lMat[0].length; ++k) {
               result[i][j] += lMat[i][k] * rMat[k][j];
            }
         }
      }

      return result;
   }

   public static double[][] matMul(int[][] lMat, double[][] rMat) {
      double[][] result = new double[lMat.length][rMat[0].length];

      for(int i = 0; i < result.length; ++i) {
         for(int j = 0; j < result[i].length; ++j) {
            for(int k = 0; k < lMat[0].length; ++k) {
               result[i][j] += (double)lMat[i][k] * rMat[k][j];
            }
         }
      }

      return result;
   }

   public static double[][] matMul(double[][] lMat, int[][] rMat) {
      double[][] result = new double[lMat.length][rMat[0].length];

      for(int i = 0; i < result.length; ++i) {
         for(int j = 0; j < result[i].length; ++j) {
            for(int k = 0; k < lMat[0].length; ++k) {
               result[i][j] += lMat[i][k] * (double)rMat[k][j];
            }
         }
      }

      return result;
   }

   public static double[][] matMul(double[][] lMat, double[][] rMat) {
      double[][] result = new double[lMat.length][rMat[0].length];

      for(int i = 0; i < result.length; ++i) {
         for(int j = 0; j < result[i].length; ++j) {
            for(int k = 0; k < lMat[0].length; ++k) {
               result[i][j] += lMat[i][k] * rMat[k][j];
            }
         }
      }

      return result;
   }

   public static int[][] matDiv(int[][] lMat, int r) {
      int[][] result = new int[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] / r;
         }
      }

      return result;
   }

   public static double[][] matDiv(int[][] lMat, double r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = (double)lMat[i][j] / r;
         }
      }

      return result;
   }

   public static double[][] matDiv(double[][] lMat, int r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] / (double)r;
         }
      }

      return result;
   }

   public static double[][] matDiv(double[][] lMat, double r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] / r;
         }
      }

      return result;
   }

   public static int[][] matMod(int[][] lMat, int r) {
      int[][] result = new int[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] % r;
         }
      }

      return result;
   }

   public static double[][] matMod(int[][] lMat, double r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = (double)lMat[i][j] % r;
         }
      }

      return result;
   }

   public static double[][] matMod(double[][] lMat, int r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] % (double)r;
         }
      }

      return result;
   }

   public static double[][] matMod(double[][] lMat, double r) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = lMat[i][j] % r;
         }
      }

      return result;
   }

   public static int[][] matNeg(int[][] lMat) {
      int[][] result = new int[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = -lMat[i][j];
         }
      }

      return result;
   }

   public static double[][] matNeg(double[][] lMat) {
      double[][] result = new double[lMat.length][lMat[0].length];

      for(int i = 0; i < lMat.length; ++i) {
         for(int j = 0; j < lMat[i].length; ++j) {
            result[i][j] = -lMat[i][j];
         }
      }

      return result;
   }

   public static int[][] matPow(int[][] mat, int k) {
      int[][] result = new int[mat.length][mat.length];

      for(int i = 0; i < result.length; ++i) {
         result[i][i] = 1;
      }

      while(k != 0) {
         if ((k & 1) == 1) {
            result = matMul(result, mat);
         }

         mat = matMul(mat, mat);
         k >>>= 1;
      }

      return result;
   }

   public static double[][] matPow(double[][] mat, int k) {
      double[][] result = new double[mat.length][mat.length];

      for(int i = 0; i < result.length; ++i) {
         result[i][i] = 1.0D;
      }

      while(k != 0) {
         if ((k & 1) == 1) {
            result = matMul(result, mat);
         }

         mat = matMul(mat, mat);
         k >>>= 1;
      }

      return result;
   }

   public static int[][] getConfactor(int[][] mat, int row, int col) {
      int[][] result = new int[mat.length - 1][mat[0].length - 1];

      for(int i = 0; i < result.length; ++i) {
         int j;
         if (i < row - 1) {
            for(j = 0; j < result[i].length; ++j) {
               if (j < col - 1) {
                  result[i][j] = mat[i][j];
               } else {
                  result[i][j] = mat[i][j + 1];
               }
            }
         } else {
            for(j = 0; j < result[i].length; ++j) {
               if (j < col - 1) {
                  result[i][j] = mat[i + 1][j];
               } else {
                  result[i][j] = mat[i + 1][j + 1];
               }
            }
         }
      }

      return result;
   }

   public static double[][] getConfactor(double[][] mat, int row, int col) {
      double[][] result = new double[mat.length - 1][mat[0].length - 1];

      for(int i = 0; i < result.length; ++i) {
         int j;
         if (i < row - 1) {
            for(j = 0; j < result[i].length; ++j) {
               if (j < col - 1) {
                  result[i][j] = mat[i][j];
               } else {
                  result[i][j] = mat[i][j + 1];
               }
            }
         } else {
            for(j = 0; j < result[i].length; ++j) {
               if (j < col - 1) {
                  result[i][j] = mat[i + 1][j];
               } else {
                  result[i][j] = mat[i + 1][j + 1];
               }
            }
         }
      }

      return result;
   }

   public static int[][] toMat(int n) {
      return new int[][]{{n}};
   }

   public static double[][] toMat(double n) {
      return new double[][]{{n}};
   }

   public static double[][] toMat(String str) {
      if (str != null && !str.isEmpty() && !str.equals("E3")) {
         if (str.startsWith("E")) {
            int size = Integer.parseInt(str.substring(1));
            double[][] result = new double[size][size];

            for(int i = 0; i < size; ++i) {
               result[i][i] = 1.0D;
            }

            return result;
         } else {
            while(str.startsWith("(")) {
               str = str.substring(1, str.length() - 1);
            }

            String[] rowStr = str.split(",,");
            int rows = rowStr.length;
            double[][] result = new double[rows][];
            int cols = -1;

            for(int i = 0; i < rows; ++i) {
               String[] colStr = rowStr[i].split(",");
               if (cols == -1) {
                  cols = colStr.length;
               } else if (cols != colStr.length) {
                  throw new RuntimeException("matrix create error: " + str);
               }

               result[i] = new double[cols];

               for(int j = 0; j < cols; ++j) {
                  result[i][j] = Double.parseDouble(colStr[j]);
               }
            }

            return result;
         }
      } else {
         return new double[][]{{1.0D, 0.0D, 0.0D}, {0.0D, 1.0D, 0.0D}, {0.0D, 0.0D, 1.0D}};
      }
   }

   public static int toNumber(int[][] mat) {
      if (mat.length != mat[0].length) {
         return 0;
      } else if (mat.length == 1) {
         return mat[0][0];
      } else if (mat.length == 2) {
         return mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0];
      } else {
         int result = 0;

         for(int i = 0; i < mat.length; ++i) {
            if (i % 2 == 0) {
               result += mat[0][i] * toNumber(getConfactor(mat, 1, i + 1));
            } else {
               result -= mat[0][i] * toNumber(getConfactor(mat, 1, i + 1));
            }
         }

         return result;
      }
   }

   public static double toNumber(double[][] mat) {
      if (mat.length != mat[0].length) {
         return 0.0D;
      } else if (mat.length == 1) {
         return mat[0][0];
      } else if (mat.length == 2) {
         return mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0];
      } else {
         double result = 0.0D;

         for(int i = 0; i < mat.length; ++i) {
            if (i % 2 == 0) {
               result += mat[0][i] * toNumber(getConfactor(mat, 1, i + 1));
            } else {
               result -= mat[0][i] * toNumber(getConfactor(mat, 1, i + 1));
            }
         }

         return result;
      }
   }

   public static double[][] matToMat(int[][] mat) {
      double[][] result = new double[mat.length][];

      for(int i = 0; i < mat.length; ++i) {
         result[i] = new double[mat[i].length];

         for(int j = 0; j < mat[i].length; ++j) {
            result[i][j] = mat[i][j];
         }
      }

      return result;
   }

   public static int[][] matToMat(double[][] mat) {
      int[][] result = new int[mat.length][];

      for(int i = 0; i < mat.length; ++i) {
         result[i] = new int[mat[i].length];

         for(int j = 0; j < mat[i].length; ++j) {
            result[i][j] = (int)mat[i][j];
         }
      }

      return result;
   }

   public static double pow(int l, int r) {
      return Math.pow(l, r);
   }
}
