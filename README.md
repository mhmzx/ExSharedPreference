# ExSharedPreference

![Maven Central](https://img.shields.io/maven-central/v/io.github.sgpublic/exsp-compiler)

This is a wrapper library for `SharedPreferences` for Android, based on Lombok (with `@Data` annotation), which allows you to manage `SharedPreferences` more elegantly.

## Quick start

1. Install the `ExSharedPreference` plugin for your Android Studio, It can be obtained in the IDEA plugin marketplace.

2. Add dependencies in `build.gradle`.

   + For Java project

     ```groovy
     dependencies {
         implementation "io.github.sgpublic:exsp-common:$latest"
         implementation "io.github.sgpublic:exsp-runtime:$latest"
         annotationProcessor "io.github.sgpublic:exsp-compiler-java:$latest"
     }
     ```
     
   + For Kotlin project
   
     ```groovy
     plugins {
         id 'kotlin-kapt'
     }
     
     dependencies {
         implementation "io.github.sgpublic:exsp-common:$latest"
         implementation "io.github.sgpublic:exsp-runtime:$latest"
         kapt "io.github.sgpublic:exsp-compiler-kotlin:$latest"
     }
     ```
   
3. Create a new class for managing `SharedPreferences`, and add `@ExSharedPreference` annotations.

   + Java

     ```java
     @ExSharedPreference(name = "test")
     public class TestPreference {
     }
     ```
   
   + Kotlin
   
     ```kotlin
     @ExSharedPreference(name = "test")
     interface TestPreference {
     }
     ```
   
     
   
4. Add member variables to this class and add the `@ExValue` annotation to set the default value.

   + Java
   
     ```java
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
     
         @ExValue(defVal = "TYPE_A")
         private Type testEnum;
     
         public enum Type {
             TYPE_A, TYPE_B;
         }
     }
     ```
   
   + Kotlin
   
     ```kotlin
     @ExSharedPreference(name = "name_of_shared_preference")
     interface TestPreference {
         @ExValue(defVal = "test")
         var testString: String
     
         @ExValue(defVal = "0")
         var testFloat: Float
     
         @ExValue(defVal = "0")
         var testInt: Int
     
         @ExValue(defVal = "0")
         var testLong: Long
     
         @ExValue(defVal = "false")
         var testBool: Boolean
     
         @ExValue(defVal = "TYPE_A")
         var testEnum: Type
     
         enum class Type {
             TYPE_A, TYPE_B;
         }
     }
     ```
   
     
   
5. Use `ExPreference.init(Context context)` method in `Application` to initialize `Context`.

   ```kotlin
   class App: Application() {
       override fun onCreate() {
           super.onCreate()
   
           ExPreference.init(this)
       }
   }
   ```

6. Now you can manage `SharedPreferences` directly using `getters`/`setters` provided by `Lombok`, enjoy it!

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

   

## Custom Type

`ExSharedPreference` allows you to save custom types into SharedPreferences, but since SharedPreferences only supports a limited number of types, we use the conversion mechanism to complete this function.

**PS: We've added special support for enum types, so you needn't to add converters for enum types.**

1. Add the required custom types to the class directly.

   **Note: The `defVal` needs to fill in the string of the original type value, not your custom type!**

   ```java
   @ExSharedPreference(name = "name_of_shared_preference")
   interface TestPreference {
       ...
       @ExValue(defVal = "-1")
       var testDate: Date
       ...
   }
   ```
   
2. Create a class that implements the Converter interface and add `@ExConverter` annotation.

   ```kotlin
   @ExConverter
   class DateConverter: Converter<Date, Long> {
       override fun toPreference(origin: Date): Long {
           return origin.time
       }
   
       override fun fromPreference(target: Long): Date {
           return Date(target)
       }
   }
   ```

   The first parameter of the `Converter` generic parameter is your custom type, and the second parameter is the type actually stored in `SharedPreferences`.

## Demo

We have a demo using `Kotlin` to demonstrate how `ExSharedPreference` used: [demo-kotlin](/demo/src/main/java/io/github/sgpublic/exsp/demo).

## API

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

+ `name`: `String`

  **(Optional)** The name of the preference to retrieve, default is the variable name with a capital letter.

+ `defVal`: `String`

  **(Required)** Value to return if this preference does not exist.

### @ExConverter

This annotation is used to mark a custom type converter for `ExSharedPreference` processing.
