package com.example.friendlyfit.Friends;

import static androidx.core.view.ViewCompat.setNestedScrollingEnabled;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendlyfit.R;
import com.example.friendlyfit.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class ChatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //////////////////////////////////////
    private ImageView profile;
    private TextView name;
    private RecyclerView messages_view;
    private RecyclerView.LayoutManager recyclerViewChatLayoutManager;
    private RecyclerView.Adapter recyclerViewChatAdapter;
    private EditText message_text;
    private ImageView photo, send, back;

    private User currentLocalUser;
    private ArrayList<String> chatEmails;
    private FirebaseFirestore db;
    private ChatRecord currentChatRecord;
    private Chat currentChat;
    private ArrayList<Message> messages;

    private IChat listener;


    public ChatFragment(User mLocalCurrentUser, ArrayList<String> chatEmails) {
        this.currentLocalUser = mLocalCurrentUser;
        this.chatEmails = chatEmails;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        messages = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("help", "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        profile = view.findViewById(R.id.chat_profile);
        name = view.findViewById(R.id.chat_name);
        messages_view = view.findViewById(R.id.messages_list);
        message_text = view.findViewById(R.id.chat_message);
        back = view.findViewById(R.id.chat_back);
        send = view.findViewById(R.id.chat_send);

        recyclerViewChatLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewChatAdapter = new MessageAdapter(messages, currentLocalUser);
        messages_view.setLayoutManager(recyclerViewChatLayoutManager);
        messages_view.setAdapter(recyclerViewChatAdapter);
        setNestedScrollingEnabled(messages_view, true);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("help", "onClick: he");
                listener.backFromChatToFriends();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = message_text.getText().toString().trim();
                if( !text.equals("")){
                    Message message = new Message(text, currentLocalUser, System.currentTimeMillis()/1000);
//            Upload it to Firebase.....
                    uploadMessageToFirebase(message);
                    message_text.setText("");
//            Hide Keyboard.......
                    try {
                        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }else{
                    message_text.setError("Can't be empty!");
                }
            }
        });

        // Fetch the current messages for this chat
        fetchCurrentMessagesForThisChat(chatEmails);

        return view;

    }

    private void fetchCurrentMessagesForThisChat(ArrayList<String> chatEmails) {
        String chatRecordID = FriendUtils.generateUniqueID(chatEmails);

        DocumentReference chatRef = db.collection("users")
                .document(currentLocalUser.getEmail())
                .collection("chats")
                .document(chatRecordID);
        //fetch the chat record ID......
        chatRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currentChatRecord = documentSnapshot.toObject(ChatRecord.class);
//                        Now, fetch all the messages from that chat room.....
                        fetchCurrentChat();
                    }
                });
    }

    private void fetchCurrentChat() {
        DocumentReference chatDocument = db.collection("chats")
                .document(currentChatRecord.getDocumentReference());

//        Get the chat document from chats.....
        chatDocument.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            currentChat = task.getResult().toObject(Chat.class);
//                        Set the chat name.....
                            name.setText(currentChat.getChatName());

                            fetchMessages(chatDocument);

                        }else{
                            Toast.makeText(getContext(), "Error loading chats", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchMessages(DocumentReference chatDocument) {
        CollectionReference messagesCollection = chatDocument.collection("messages");
        messagesCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Log.e("demo", "onEvent: "+ error.getMessage());
                }else{
                    messages.clear();
                    for(DocumentSnapshot documentSnapshot: value.getDocuments()){
                        messages.add(documentSnapshot.toObject(Message.class));
                    }
                    Collections.sort(messages, new Comparator<Message>() {
                        @Override
                        public int compare(Message t1, Message t2) {
                            return (int) ((t1.getTimestamp()-t2.getTimestamp()));
                        }
                    });
                    Log.d("help", "onEvent: "+messages);
                    recyclerViewChatAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void uploadMessageToFirebase(Message message) {
        db.collection("chats")
                .document(currentChatRecord.getDocumentReference())
                .collection("messages")
                .add(message);
        db.collection("users")
                .document(currentLocalUser.getEmail())
                .collection("chats")
                .document(currentChatRecord.getDocumentReference())
                .collection("messages")
                .add(message);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IChat){
            this.listener = (IChat) context;
        }else{
            throw new RuntimeException(context.toString()+ "must implement IChat)");
        }
    }
}