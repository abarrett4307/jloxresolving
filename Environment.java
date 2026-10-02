package jlox;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

class Environment {
  final Environment enclosing;
  private final List<Object> values = new ArrayList<>();
  private final List<String> keys = new ArrayList<>();
  private final HashSet<String> usage = new HashSet<>();


  Environment() {
    enclosing = null;
  }

  Environment(Environment enclosing) {
    this.enclosing = enclosing;
  }

  Object get(int slot) {
    return values.get(slot);

    if (enclosing != null) return enclosing.get(name);

    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }

  void assign(int slot, Object value) {
    
    values.set(index, value);
    return;
    

    if (enclosing != null) {
      enclosing.assign(name, value);
      return;
    }
    
    throw new RuntimeError(name,
        "Undefined variable '" + name.lexeme + "'.");
  }
  
  void define(String name, Object value) {
    values.add(value);
    keys.add(name);
  }

  Environment ancestor(int distance) {
    Environment environment = this;
    for (int i = 0; i < distance; i++) {
      environment = environment.enclosing; 
    }

    return environment;
  }

  Object getAt(int distance, int slot) {
    return ancestor(distance).values.get(slot);
  }

  void assignAt(int distance, int slot, Object value) {
    ancestor(distance).assign(slot, value);
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