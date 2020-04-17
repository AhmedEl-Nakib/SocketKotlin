package com.shoohna.soketkotlin

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.callbacks.PNCallback
import com.pubnub.api.callbacks.SubscribeCallback
import com.pubnub.api.models.consumer.PNPublishResult
import com.pubnub.api.models.consumer.PNStatus
import com.pubnub.api.models.consumer.pubsub.PNMessageResult
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configure PubNub
         val pnConfiguration = PNConfiguration()
        pnConfiguration.subscribeKey = "sub-c-fcbdfcda-80ec-11ea-881d-66486515f06e"
        pnConfiguration.publishKey = "pub-c-4f61724e-0636-4f54-a2e2-fb7a4b76d02a"
        val pubNub = PubNub(pnConfiguration)

        // Views
        val publishText = findViewById<EditText>(R.id.editTextPublish)
        val button = findViewById<Button>(R.id.button)
        val list = findViewById<ListView>(R.id.lvId)
        val arrayList : ArrayList<String> = ArrayList<String>()
        var arrayAdapter:ArrayAdapter<String>

        button.setOnClickListener {
            pubNub.run {
                publish()
                    .message(publishText.text.toString())
                    .channel("whiteboard")
                    .async(object : PNCallback<PNPublishResult>() {
                        override fun onResponse(result: PNPublishResult, status: PNStatus) {
                            if (!status.isError) {
//                                    println("Message was published")
                                Toast.makeText(applicationContext, "Message was published", Toast.LENGTH_SHORT).show()
                                publishText.text.clear()
                            } else {
//                                    println("Could not publish")
                                Toast.makeText(applicationContext, "Could not publish", Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }
        }



        var subscribeCallback: SubscribeCallback = object : SubscribeCallback() {
            override fun status(pubnub: PubNub, status: PNStatus) {
            }

            override fun message(pubnub: PubNub, message: PNMessageResult) {
                runOnUiThread {
                    arrayList.add(message.message.toString().replace("\"",""))
                    arrayAdapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, arrayList)
                    list.adapter = arrayAdapter
                }
            }

            override fun presence(pubnub: PubNub, presence: PNPresenceEventResult) {
            }
        }
        pubNub.run{
            addListener(subscribeCallback)
            subscribe()
                .channels(listOf("whiteboard")) // subscribe to channels
                .execute()

        }

    }

}
