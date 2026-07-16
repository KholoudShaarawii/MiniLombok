# MiniLombok

MiniLombok is a Java project that demonstrates how annotation processors can generate getter and setter methods during compilation by modifying the Java compiler Abstract Syntax Tree (AST).

The project provides two custom annotations:

- `@Accessor` — generates getter methods.
- `@Mutator` — generates setter methods.

## How It Works

During Maven compilation, the custom annotation processor:

1. Detects classes annotated with `@Accessor` or `@Mutator`.
2. Reads the annotated class and its fields using the Java Annotation Processing API.
3. Accesses the `javac` compiler AST.
4. Creates getter or setter method nodes using `TreeMaker`.
5. Adds the generated methods to the class AST before bytecode generation.

The processor modifies the class during compilation and does not create additional `.java` source files.

## Example

```java
@Accessor
@Mutator
public class User {

    String name;
}
```

The following methods become available after compilation:

```java
public String getName();
public void setName(String name);
```

They can then be used normally:

```java
User user = new User();

user.setName("Kholoud");
System.out.println(user.getName());
```

## Project Structure

```text
MiniLombok
├── LombokProcessor
│   ├── src/main/java
│   │   └── LombokProcessor.java
│   ├── src/main/resources
│   │   └── META-INF/services
│   │       └── javax.annotation.processing.Processor
│   └── pom.xml
│
├── app
│   ├── src/main/java/com/kho
│   │   ├── annotations
│   │   │   ├── Accessor.java
│   │   │   └── Mutator.java
│   │   ├── Main.java
│   │   └── User.java
│   └── pom.xml
│
└── pom.xml
```

### Modules

- **LombokProcessor:** Implements and registers the custom annotation processor.
- **app:** Contains the annotations and a simple example that uses the generated methods.
- **Root project:** Builds both Maven modules in the required order.

## Technologies

- Java 11
- Maven
- Java Annotation Processing API
- Java Compiler Tree API
- `javac` AST internals
- Java Service Provider configuration

## Build

Build the project from the root directory:

```bash
mvn clean install
```

The root Maven project builds the annotation processor before compiling the application.

When using IntelliJ IDEA without Maven installed globally:

```text
Maven Tool Window
→ lombok Root
→ Lifecycle
→ clean
→ install
```

## Run

After a successful build, run:

```text
app/src/main/java/com/kho/Main.java
```

Or from the command line:

```bash
java -cp app/target/classes com.kho.Main
```

## Generated Methods

After compilation, the generated getter and setter methods are included in the compiled `User.class` file under:

```text
app/target/classes
```

The `target` directories are Maven-generated build output and are intentionally excluded from version control.

## Current Limitations

- Supports basic getter and setter generation only.
- Uses internal `javac` APIs and is configured for Java 11.
- Generated methods are stored in compiled bytecode rather than separate Java source files.
- The project is a focused demonstration of compile-time code generation, not a full replacement for Lombok.
