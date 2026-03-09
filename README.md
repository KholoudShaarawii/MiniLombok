# Mini Lombok - Custom Annotation Processor

A lightweight custom annotation processor inspired by Lombok, built using Java Annotation Processing and Javac AST internals.

## Overview

This project demonstrates how to build a custom annotation processor that generates accessor and mutator methods at compile time.

Instead of writing getters and setters manually, the processor scans classes annotated with custom annotations and injects methods directly into the Abstract Syntax Tree (AST) during compilation.

The project is mainly educational and focuses on understanding:

- Java Annotation Processing API
- Javac internal compiler APIs
- AST inspection and modification
- Compile-time code generation

## Supported Annotations

The processor currently supports:

- `@Accessor` → generates getter methods
- `@Mutator` → generates setter methods


- If the class uses `@Accessor`, a getter is generated:
  ```java
  public Type getFieldName() {
      return this.fieldName;
  }
  ```

- If the class uses `@Mutator`, a setter is generated:
  ```java
  public void setFieldName(Type fieldName) {
      this.fieldName = fieldName;
  }
  ```

## Example

### Input

```java
@Accessor
@Mutator
public class User {
    private String name;
    private int age;
}
```

### Generated Methods

```java
public String getName() {
    return this.name;
}

public void setName(String name) {
    this.name = name;
}

```

## Technologies Used

- Java
- Annotation Processing API
- Javac Compiler Internals
- AST Manipulation

## Key Javac Classes Used

This project uses several internal Javac classes, such as:

- `JavacProcessingEnvironment`
- `TreeMaker`
- `JCTree`
- `Names`
- `Flags`

These classes make it possible to inspect and modify Java source structure before bytecode generation.

## Project Goal

The main goal of this project is educational.

It helps in understanding the difference between:

- Runtime reflection
- Compile-time code generation

It also explains how tools like Lombok work internally on a smaller scale.

## Processing Flow

1. Compiler starts annotation processing
2. The processor initializes compiler tools
3. Annotated classes are discovered
4. Class fields are inspected
5. Getter/setter methods are generated
6. Modified AST continues through normal compilation

## Requirements

- JDK 11 or compatible setup
- Maven or any Java build tool that supports annotation processing


## Learning Outcomes

This project highlights hands-on experience with Java annotation processing, compiler internals, and AST-based code generation.

It also demonstrates:
- how annotation processors are discovered and executed
- how Java compiler rounds work
- how AST nodes represent classes, fields, and methods
- how code can be generated before the program is compiled

  
## Why This Project Matters

This project demonstrates a deeper level of Java understanding by focusing on compile-time code generation rather than standard application development.

It shows how Java source code can be inspected and modified inside the compiler using annotation processing and AST manipulation, which are core ideas behind advanced developer tools and libraries such as Lombok.

As a result, the project reflects practical experience with compiler internals, code generation, and framework-level development concepts.

## Author

Developed as an educational project to explore Java annotation processing, compiler internals, and AST-based code generation through a mini Lombok-style implementation

Created as a learning project to explore Java compiler internals and build a mini Lombok-style processor.
