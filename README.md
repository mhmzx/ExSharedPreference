# ExSharedPreference

This is a wrapper library for `SharedPreferences` for Android, based on Lombok (with `@Data` annotation), which allows you to manage `SharedPreferences` more elegantly.

## Quick start

1. Install the `Lombok` plugin for your Android Studio according to the method provided in this repository: [sgpublic/lombok-plugin-action: A repository for Lombok plugin incompatibility issues with Android Studio. (github.com)](https://github.com/sgpublic/lombok-plugin-action)

2. Add dependencies in `build.gradle`.

   ```groovy
   dependencies {
       implementation "io.github.sgpublic:exsp-runtime:$latest"
       annotationProcessor "io.github.sgpublic:exsp-compiler:$latest"
       
       def lombok_ver = "1.18.24"
       compileOnly "org.projectlombok:lombok:$lombok_ver"
       annotationProcessor "org.projectlombok:lombok:$lombok_ver"
   }
   ```

3. If your project is using `Kotlin`, you also need to add to your `build.gradle`:

   ```groovy
   plugins {
       id 'org.jetbrains.kotlin.plugin.lombok' version '1.7.10'
       id 'io.freefair.lombok' version '5.3.0'
   }
   
   kapt {
       keepJavacAnnotationProcessors = true
   }
   ```

   See [Lombok compiler plugin | Kotlin (kotlinlang.org)](https://kotlinlang.org/docs/lombok.html#using-with-kapt) for more details.

4. Create a new class for managing `SharedPreferences`, and add `@ExSharedPreference` and `@Data` annotations.

   **PS: Whether your project uses `Java` or `Kotlin`, this class must be `Java`!**

   ```java
   @Data
   @ExSharedPreference(name = "test")
   public class TestPreference {
   }
   ```

5. Add member variables to this class and add the `@ExValue` annotation to set the default value.

   ```java
   @Data
   @ExSharedPreference(name = "name_of_shared_preference")
   public class TestPreference {
       @ExValue(defVal = "test")
       private String testString;
       
       @ExValue(defVal = "0")
       private float testFloat;
       
       @ExValue(defVal = "0")
       private int testInt;
       
       @ExValue(defVal = "0")
       private long testLong;
       
       @ExValue(defVal = "false")
       private boolean testBool;
   }
   ```

6. Use `ExPreference.init(Context context)` method in `Application` to initialize `Context`.

   ```kotlin
   class App: Application() {
       override fun onCreate() {
           super.onCreate()
           
           ExPreference.init(this)
       }
   }
   ```

7. Now you can manage `SharedPreferences` directly using `getters`/`setters` provided by `Lombok`, enjoy it!

   + Kotlin

     ```kotlin
     val test: TestPreference = ExPreference.get()
     test.testString = "new string"
     Log.d("TestPreference#testString", test.testString)
     ```

   + Java

     ```java
     TestPreference test = ExPreference.get(TestPreference.class);
     test.setTestString("new string");
     Log.d("TestPreference#testString", test.getTestString());
     ```

## Demo

We have a demo using `Kotlin` to demonstrate how `ExSharedPreference` used: [demo-kotlin](/demo/src/main/java/io/github/sgpublic/exsp/demo).

## Customize

### @ExSharedPreference

This annotation is used to create a `SharedPreferences` whose parameters correspond to the `Context#getSharedPreferences(String name, int mode)` method.

That is, the following annotation:

```java
@ExSharedPreference(name = "name_of_shared_preference", mode = Context.MODE_PRIVATE)
```

means the following statement:

```java
SharedPreferences sharedPreference = context.getSharedPreference("name_of_shared_preference", Context.MODE_PRIVATE);
```

#### Parameter explanation

+ `name`: `String`

  **(Required)** Desired preferences file.

+ `mode`: `int`/`Intefer`

  **(Optional)** Operating mode, default is `Context.MODE_PRIVATE`.

### @ExValue

This annotation is used to mark a `SharedPreference` key whose parameters correspond to the `getXxxx` methods of class `android.content.SharedPreferences`.

That is, the following annotation:

```java
@ExValue(name = "test_string", defVal = "default value")
private String testString;
```

means the following statement:

```java
// getter
sharedPreference.getString("test_string", "default value");
// setter
sharedPreference.editor()
    .putString("test_string", "new value")
    .apply();
```

#### Parameter explanation

+ `key`: `String`

  **(Optional)** The name of the preference to retrieve, default is the variable name with a capital letter.

+ `defVal`: `String`

  **(Required)** Value to return if this preference does not exist.