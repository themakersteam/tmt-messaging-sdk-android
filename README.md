# Android-Message-Center


## Android messaging library

### 1. Setup
* Add jitpack dependency to root build.gradle
    ```bash
        	allprojects {
        		repositories {
        			...
        			maven { url 'https://jitpack.io' }
        		}
        	}
     ```

 * Add the dependency
    ```bash
	    dependencies {
	        implementation 'com.tmt.messagecenter:android-message-center:{latest-version}'
	}
    ```

### 2. Sample App

  * For Debugging/Testing the library (to be able to run the library as an application) do the following:

  * Replace The `` build.gradle `` plugin from ``com.android.library`` to ``com.android.application``

  * Uncomment the Manifest ``todo: Uncomment for testing ``

  * Test and debug method inside ``TestActivity.Java``, Default Launcher for the app

### 3. Usage

#### 3.0 Design
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

#### 3.1 connect()

 * First Step for integrating the app is to connect on the start of the application

     ```bash
    MessageCenter.connect(Context context, ConnectionRequest connection, ConnectionInterface connectionInterface)
     ```

 * Connection Request Object Has the following items

    *    String username; 
    *    String password;
    *    String domain; 

 * Sample Code for connecting to Message Center

   ```bash
       MessageCenter.connect(this, connectionRequest, new ConnectionInterface() {
                      @Override
                      public void onConnected() {

                      }

                      @Override
                      public void onConnectionError(int error_code, Exception e) {

                      }
                  });
   ```

#### 3.2 getUnReadMessagesCount()
 * Getting Total of Unread Messages

      ```bash
     MessageCenter.getUnReadMessagesCount(Context context, String chat_id, UnReadCountInterface unread_message_interface)
      ```
 * if chat_id must be provided

#### 3.3 openChatView()
 * Joining the chat by url(id) provided
 * Sample code for joining a conversation
    ```bash
    MessageCenter.openChatView(Activity: this, chat_id: "sample_chat_id", theme: new Theme(toolbar: "title", toolbar_subtitle: "subtitle"), openChatViewInterface: OpenChatViewInterface);
    ```
    
 * if Theme object is not provided, the app will take the defaults
 * Theme Object for android have (```app_name```,```toolbar```, ```toolbar_subtitle```, ```welcome_message```)
 * Executing this interface will open the chatting window
 * an error callback will be triggered in case of error
 * onActivityResult will be triggered on the close of the Chat View with request_code: MessageCenter.OPEN_CHAT_VIEW_REQUEST_CODE, response_code: MessageCenter.OPEN_CHAT_VIEW_RESPONSE_CODE

#### 3.4 isConnected()

 * returns true if Message Center is connected

 * Sample code for checking connection
    ```bash
    MessageCenter.isConnected();
    ```

#### 3.5 disconnect()

 * Disconnects the chat services, best case to use if with user logout

 * Sample code for disconnecting
    ```bash
    MessageCenter.disconnect(Context context, new DisconnectInterface() {
                @Override
                public void onMessageCenterDisconnected() {

                }
            });
    ```
