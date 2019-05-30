

# PixEditor   (WhatsApp Style Image Editor)

PixEditor is a Whatsapp image Editor replica. with this you can integrate a image Editor just like whatsapp.




## Usage

```groovy
   editOptions = EditOptions.init().apply {
              requestCode = RequestCodeEditor
              selectedlist = returnValue                    //Custom Path For Image Storage
              addMoreImagesListener = this@MainActivity
              }

              PixEditor.start(this@MainActivity, editOptions)

```



Use onActivityResult method to get results
```groovy
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
               if (resultCode == Activity.RESULT_OK && requestCode1 == RequestCodeEditor) {
                          val returnValue = data!!.getStringArrayListExtra(PixEditor.IMAGE_RESULTS)
                      }
        }
```

 include in app level build.gradle
 ```groovy
        repositories {
           maven { url 'https://jitpack.io' }
        }
 ```
```groovy
        implementation  'com.fxn769:pix-editor:1.0.4'
```
or Maven:
```xml
        <dependency>
          <groupId>com.fxn769</groupId>
          <artifactId>pix-editor</artifactId>
          <version>1.0.4</version>
          <type>pom</type>
        </dependency>
```
or ivy:
```xml
        <dependency org='com.fxn769' name='pix-editor' rev='1.0.4'>
          <artifact name='pix-editor' ext='pom' ></artifact>
        </dependency>
```


## License
Licensed under the Apache License, Version 2.0, [click here for the full license](/LICENSE).

## Author & support
This project was created by [Akshay Sharma](https://akshay2211.github.io/).

> If you appreciate my work, consider buying me a cup of :coffee: to keep me recharged :metal: by [PayPal](https://www.paypal.me/akshay2211)

> I love using my work and I'm available for contract work. Freelancing helps to maintain and keep [my open source projects](https://github.com/akshay2211/) up to date!

