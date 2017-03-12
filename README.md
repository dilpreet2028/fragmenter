# fragmenter
Generates boilerplate code to create arguments for a fragment.
+ Eliminates need to create a static function to initialize a fragment
+ Eliminates need to bind the arguments manually

### Example

#####Fragment Class

```java
import com.dilpreet2028.fragmenter_annotations.Fragmenter;
import com.dilpreet2028.fragmenter_annotations.annotations.Arg;
import com.dilpreet2028.fragmenter_annotations.annotations.FragModule;

@FragModule//Need to annotate fragment with @FragModule
public class DemoFragment extends Fragment {
    
    @Arg
    String data; //Annotate variables needed to be initialised with @Arg

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_demo, container, false);
        
        Fragmenter.inject(this);//arguments gets injected automically

        ((TextView) view.findViewById(R.id.tv_text)).setText(data);

        return view;
    }

}
```
**Note: After creating a fragment build the project to allow fragmenter to generate the classes for you.**

Fragmenter generates a Builder class i.e. the name of your Fragment with "Builder" as suffix .<br>
In this example Fragmenter creates a `DemoFragmentBuilder` for `DemoFragment`

####Activity Class

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        String data = "Hello world";
         
        //using the builder class and passing the required variables.
        DemoFragment fragment = DemoFragmentBuilder.newInstance(data);
        
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content , fragment) 
                .commit();


    }
}
```

##Download
In the root build.gradle file add : 
```
allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
```
In your app build.gradle file add:

```
dependencies {
    compile 'com.github.dilpreet96.fragmenter:fragmenter-annotations:1.0.1'
    annotationProcessor 'com.github.dilpreet96.fragmenter:fragmenter-compiler:1.0.1'
    }
```

###Under construction
+ Support ArrayList in Arguments 

### License
```
Copyright (C) 2017 Dilpreet Singh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
