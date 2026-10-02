package jlox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

class Environment {
  final Environment enclosing;
  private final Map<String, Object> values = new HashMap<>();
  private final HashSet<String> usage = new HashSet<>();


  Environment() {
    enclosing = null;
  }

  Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  Object get(Token name) {
    if (values.containsKey(name.lexeme)) {
      usage.add(values.get(name.lexeme).toString());
      return values.get(name.lexeme);
    }

    if (enclosing != null) return enclosing.get(name);

    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }

  void assign(Token name, Object value) {
    if (values.containsKey(name.lexeme)) {
      values.put(name.lexeme, value);
      return;
    }

    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }
    
    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }
  
  void define(String name, Object value) {
    values.put(name, value);
  }

  Environment ancestor(int distance) {
    Environment environment = this;
    for (int i = 0; i < distance; i++) {
      environment = environment.enclosing; 
    }

    return environment;
  }

  Object getAt(int distance, String name) {
    return ancestor(distance).values.get(name);
  }

  void assignAt(int distance, Token name, Object value) {
    ancestor(distance).values.put(name.lexeme, value);
  }

  public void checkUnused() {
    for (String key : values.keySet()) {
      if (!usage.contains(values.get(key).toString())) {
        RuntimeError error = new RuntimeError(new Token(TokenType.IDENTIFIER, key, null, 0),
            "Warning: Variable '" + key + "' is defined but never used.");
        Lox.runtimeError(error);
      }
    }
  }
}