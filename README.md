# Android-Message-Center


## Android messaging library

### 1. SDK
* Add Your Github username, authToken to gradle.properties (Global Properties)
    ```bash
            gitToken=token
            gitUser=github_user_name
    ```
* To generate access token please check: https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line

* To publish a new version upgrade the version and 
    ```bash
            ./gradlew clean build publish
     ```
     
### 2. Setup
* Add maven dependency to root build.gradle
    ```bash
        	allprojects {
        		repositories {
        			...
        			 maven {
                            credentials {
                                    username gitUser
                                    password gitToken
                            }
                     url "https://maven.pkg.github.com/themakersteam/tmt-live-chat-sdk-android"
                    }
        		}
        	}
     ```

 * Add the dependency
    ```bash
	    dependencies {
	        implementation 'com.tmt:messagecenter:{latest-version}'
	}
    ```

### 3. Sample App

  * For Debugging/Testing the library (to be able to run the library as an application) do the following:

  * Replace The `` build.gradle `` plugin from ``com.android.library`` to ``com.android.application``

  * Uncomment the Manifest ``todo: Uncomment for testing ``

  * Test and debug method inside ``TestActivity.Java``, Default Launcher for the app

### 4. Usage

#### 4.0 Design
 * For toolbar title override the following string to strings.xml - strings-ar.xml

    ```bash
     <string name="message_center_toolbar_title">Title</string>
     <string name="message_center_channel_is_frozen">Frozen Channel Title</string>
    ```
 * For colors styling override to colors.xml

     ```bash
     <color name="message_center_primary">{color}</color>
     <color name="message_center_primary_dark">{color}</color>
     <color name="message_center_primary_accent">{color}</color>
     <color name="message_center_chat_view_background">{color}</color>
     <color name="message_center_chat_view_welcome_background">{color}</color>
     <color name="message_center_chat_view_bubble_color">{color}</color>
     ```

#### 4.1 connect()

 * First Step for integrating the app is to connect on the start of the application

 * You will have to deal with multiple project firebase app : https://firebase.google.com/docs/projects/multiprojects#use_multiple_projects_in_your_application
 * make sure you pass The FirebaseApp instance for Tmt Messaging 
     * FirebaseApp.initializeApp(this /* Context */, options, "tmt-messaging");
     * FirebaseApp messaging = FirebaseApp.getInstance("tmt-messaging");
 
     ```bash
    Livechat.connect(FirebaseApp app, ConnectionRequest connection, ConnectionInterface connectionInterface)
     ```

 * Connection Request Object Has the following items

    *    String authToken; 

 * Sample Code for connecting to Message Center

   ```bash
       Livechat.connect(messaging, connectionRequest, new ConnectionInterface() {
                      @Override
                      public void onConnected() {

                      }

                      @Override
                      public void onConnectionError(int error_code, Exception e) {

                      }
                  });
   ```

#### 4.2 getUnReadMessagesCount()
 * Getting Total of Unread Messages

      ```bash
     Livechat.getUnReadMessagesCount(String chat_id, UnReadCountInterface unread_message_interface)
      ```
 * chat_id must be provided

#### 4.3 openChatView()
 * Sample code for joining a conversation
    ```bash
    Livechat.openChatView(Activity: this, chat_id: "sample_chat_id", theme: new Theme(toolbar: "title", toolbar_subtitle: "subtitle"), openChatViewInterface: OpenChatViewInterface);
    ```
    
 * if Theme object is not provided, the app will take the defaults
 * Theme Object for android have (```app_name```,```toolbar```, ```toolbar_subtitle```, ```welcome_message```)
 * Executing this interface will open the chatting window
 * an error callback will be triggered in case of error
 * onActivityResult will be triggered on the close of the Chat View with request_code: Livechat.OPEN_CHAT_VIEW_REQUEST_CODE, response_code: Livechat.OPEN_CHAT_VIEW_RESPONSE_CODE

#### 4.4 isConnected()

 * returns true if Message Center is connected

 * Sample code for checking connection
    ```bash
    Livechat.isConnected();
    ```
    
#### 4.5 isPresented()

 * returns true if Message Center is presented to the user
 * Used for notifications, don't show notification when its presented 

 * Sample code for checking connection
    ```bash
    Livechat.isPresented();
    ```
    
#### 4.6 disconnect()

 * Disconnects the chat services, best case to use if with user logout
