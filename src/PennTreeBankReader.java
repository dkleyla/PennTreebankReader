/*-
 * PennTreebankReader PennTreeBankReader.java
 * 
 * Copyright (c) 2012, Yifan Peng 
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Yifan Peng  BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
import java.io.IOException;
import java.io.InputStreamReader;

public class PennTreeBankReader {

  InputStreamReader in;
  TreeBankNode      next;
  int               currentChar;

  public PennTreeBankReader(InputStreamReader in)
      throws IOException {
    this.in = in;
    currentChar = nextChar();
    next();
  }

  public boolean hasNext() {
    return next != null;
  }

  public TreeBankNode next()
      throws IOException {
    TreeBankNode curr = next;
    next = readNext();
    return curr;
  }

  private int nextChar()
      throws IOException {
    return in.read();
  }

  private String nextToken()
      throws IOException {
    if (currentChar == -1) {
      return null;
    }
    if (currentChar == '(' || currentChar == ')') {
      String s = Character.toString((char) currentChar);
      currentChar = nextChar();
      return s;
    }

    // white space
    while (Character.isWhitespace(currentChar)) {
      currentChar = nextChar();
    }

    if (currentChar == -1) {
      return null;
    }
    if (currentChar == '(' || currentChar == ')') {
      String s = Character.toString((char) currentChar);
      currentChar = nextChar();
      return s;
    }
    StringBuilder sb = new StringBuilder();
    sb.append((char) currentChar);
    currentChar = nextChar();
    while (currentChar != '('
        && currentChar != ')'
        && currentChar != -1
        && !Character.isWhitespace(currentChar)) {
      sb.append((char) currentChar);
      currentChar = nextChar();
    }
    return sb.toString();

  }

  private TreeBankNode readNext()
      throws IOException {
    TreeBankNode parent = new TreeBankNode();

    int state = 0;
    while (true) {
      String s = nextToken();
      switch (state) {
      case 0:
        if (s == null) {
          // throw exception
          return null;
        } else if (s.equals("(")) {
          TreeBankNode child = new TreeBankNode();
          parent.add(child);
          parent = child;
          state = 1;
        } else {
          return null;
        }
        break;
      case 1:
        if (s == null) {
          // throw exception
          return null;
        } else if (s.equals("(") || s.equals(")")) {
          // throw exception
          return null;
        } else {
          parent.tag = s;
          state = 2;
        }
        break;
      case 2:
        if (s == null) {
          // throw exception
          return null;
        } else if (s.equals("(")) {
          TreeBankNode child = new TreeBankNode();
          parent.add(child);
          parent = child;
          state = 1;
        } else if (s.equals(")")) {
          // throw exception
          return null;
        } else {
          parent.word = s;
          state = 3;
        }
        break;
      case 3:
        if (s == null) {
          // throw exception
          return null;
        }
        if (s.equals(")")) {
          if (parent == null) {
            // throw exception
            return null;
          }
          parent = parent.getParent();
          if (parent.getParent() == null) {
            return parent;
          }
        } else if (s.equals("(")) {
          TreeBankNode child = new TreeBankNode();
          parent.add(child);
          parent = child;
          state = 1;
        }
        break;
      }
    }
  }
}
