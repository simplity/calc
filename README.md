# Programming Notes – Project Structure and Guidelines

**Project Name:** *Simplity Calculator*  
**Java Version:** 1.8  
**Build System:** Maven  
**IDE:** Eclipse

---

## 🔧 Organization Principles

- The project uses **multiple source folders** under `src/` to organize the code logically.
- There are only two packages: `org.simplity.calc.api` and `org.simplity.calc.implmpl`.
- All the interfaces in `api` and `config` are public. Only a small subset of classes that are required for the end-users are scoped as `public` in `impl`. All other classes are default scoped.
- This allows us to use **package-private visibility** across the codebase, enabling encapsulation without the need for `public` on internal classes.
- Internal modularity is enforced through **folder layout**, not package hierarchy.

---

## 📁 Folder Structure

```
src/
├── main/
│   └── java/                         # Shared base if needed
├── public/                           # all classes in this folder are public
│   └── java/
│       └── org/simplity/calc/api/
│                            │   └── ICalcEngineFActoty.java
│                            └impl/
│                                 └── CalcFactory.java
│                            └config/
│                                 └── CalcConfig.java
├── expr/
│   └── java/
│       └── org/simplity/calc/impl/
│                                 └── FunctionExpression.java
├── schema/
│   └── java/
│       └── org/simplity/calc/impl/
│                                 └── DateValidator.java
```

---

## 📦 Package Convention

- **All classes share the package:**  
  classes in non-public folder share the package `package com.simplity.calc.impl;`

- **Visibility Guidelines:**
  - Use `public` **only** for classes in the `public` folder structure that are meant to be exposed to the users of our utility JAR file.
  - Default/package-private visibility should be used for all internal implementation classes.
  - Internal classes can interact freely across folders due to shared package.
  - private static classes if sub-classes are used by a main-class that are not to be exposed to otehr classes within our code-base

---

## 🛠 Build Notes

- Use the **`build-helper-maven-plugin`** in `pom.xml` to add all the non-standard source folders (like `public`, etc.).
- In Eclipse, go to **Project → Properties → Java Build Path → Source**, and add each folder as a source folder.
- Maven and Eclipse will treat all added source folders equally.

---

## 🧠 Rationale

- This setup **maximizes encapsulation** in Java 8, where modularization tools like JPMS are not available.
- Keeps the **JAR’s public surface minimal**, aiding maintainability and clean design.
- Avoids cluttering the package with hundreds of public classes.
- Encourages logical, domain-based organization via folders, without fragmenting the package structure.

---

## 👋 Notes for New Developers

- When adding a new feature or component, create a **new source folder** (e.g., `src/transformers/java/...`) and place its classes in the common package.
- Always use the consistent package declaration:  
  `package org.simplity.calc.impl;`
- Do **not** create sub-packages unless there's a very compelling reason (such as integration with external frameworks or legacy code).
- Organize the code by **logical folder boundaries**, not by package names.
- Keep internal classes **non-public** by default — only elevate to `public` if explicitly intended for API consumers.