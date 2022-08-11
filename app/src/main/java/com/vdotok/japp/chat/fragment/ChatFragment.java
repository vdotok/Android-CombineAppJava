package com.vdotok.japp.chat.fragment;

import static com.vdotok.japp.constants.ApplicationConstants.FILE_SIZE_LIMIT;
import static com.vdotok.japp.constants.ApplicationConstants.TYPING_START;
import static com.vdotok.japp.constants.ApplicationConstants.TYPING_STOP;
import static com.vdotok.japp.constants.ApplicationConstants.directoryName;
import static com.vdotok.japp.constants.ApplicationConstants.docMimeType;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

import com.vdotok.connect.models.HeaderModel;
import com.vdotok.connect.models.Message;
import com.vdotok.connect.models.MessageType;
import com.vdotok.connect.models.ReadReceiptModel;
import com.vdotok.connect.models.ReceiptType;
import com.vdotok.connect.utils.ImageUtils;
import com.vdotok.japp.R;
import com.vdotok.japp.base.BaseFragment;
import com.vdotok.japp.chat.adapter.ChatAdapter;
import com.vdotok.japp.chat.dialogs.ChatAttachmentDialogFragment;
import com.vdotok.japp.chat.dialogs.OnAttachmentClickListener;
import com.vdotok.japp.chat.dialogs.StoragePermissionDialogFragment;
import com.vdotok.japp.chat.enums.FileSelectionEnum;
import com.vdotok.japp.chat.enums.MimeTypeEnum;
import com.vdotok.japp.chat.interfaces.OnCompleteActionListener;
import com.vdotok.japp.chat.interfaces.OnNewMessageListener;
import com.vdotok.japp.chat.viewmodel.ChatViewModel;
import com.vdotok.japp.dashboard.interfaces.OnStoragePermissionListener;
import com.vdotok.japp.databinding.FragmentChatBinding;
import com.vdotok.japp.manager.AppManager;
import com.vdotok.japp.network.models.GroupModel;
import com.vdotok.japp.network.setup.APICallObserversEnum;
import com.vdotok.japp.utils.ChatFileUtils;
import com.vdotok.japp.utils.FileUtils;
import com.vdotok.japp.utils.OnSingleClickListener;
import com.vdotok.japp.utils.viewExtension;
import com.vdotok.streaming.enums.MediaType;
import com.vdotok.streaming.enums.SessionType;
import com.vdotok.streaming.models.CallParams;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.EasyPermissions;

public class ChatFragment extends BaseFragment<ChatViewModel, FragmentChatBinding> implements EasyPermissions.PermissionCallbacks {
    View view;
    ChatAdapter adapter;
    private int fileType = 0;
    File selectedFile = null;
    File selectedFileCamera = null;
    byte[] byteArray;
    public ObservableField<String> messageText = new ObservableField<>();
    public ObservableBoolean disableButton = new ObservableBoolean(false);
    public ObservableField<String> typingText = new ObservableField<>();
    public ObservableBoolean showTypingText = new ObservableBoolean(false);

    @Override
    public void onObserve(APICallObserversEnum event, Object eventMessage) {
        //viewExtension.INSTANCE.showSnackBar(binding.getRoot(), eventMessage.toString());
    }

    @Override
    public Class<ChatViewModel> getViewModel() {
        return ChatViewModel.class;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_chat;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        setBinding();
        setUi();
        setClickListeners();
        setAdapter();
        getChat();
        return view;
    }

    public void getChat() {
        adapter.updateData(viewModel.getAppManager().mapGroupMessages.get(viewModel.getAppManager().groupModel.getChannelName()));
        scrollToLast();
    }

    private void setUi() {
        binding.progressBar.setVisibility(View.GONE);
        messageText.set("");
        if (messageText.get().isEmpty()) {
            disableButton.set(true);
        }
        binding.customBottombar.edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty()) {
                    disableButton.set(true);
                } else {
                    disableButton.set(false);
                    handleAfterTextChange(editable.toString());
                }

            }
        });

    }

    private void handleAfterTextChange(String message) {
        if (!message.isEmpty()) {
            sendTypingMessage(viewModel.getAppManager().getUserData().getRefId().toString(), true);
            new CountDownTimer(1500, 1000) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    sendTypingMessage(viewModel.getAppManager().getUserData().getRefId(), false);
                }
            }.start();
        }
    }

    private void setAdapter() {
        adapter = new ChatAdapter(requireContext(), new ArrayList(), viewModel);
        adapter.setHasStableIds(true);
        binding.rcvMsgList.setAdapter(adapter);
        binding.rcvMsgList.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (bottom < oldBottom) scrollToLast();
        });
        scrollToLast();
    }

    /**
     * Function to scroll recyclerview to last index
     */
    private void scrollToLast() {
        if (adapter.getItemCount() > 0)
            binding.rcvMsgList.smoothScrollToPosition(adapter.getItemCount());
    }

    private void setClickListeners() {
        binding.customToolbar.arrowBack.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void performClick(View v) {
                requireActivity().onBackPressed();
            }
        });
        binding.customBottombar.sendButton.setOnClickListener(view1 -> {
            if (!messageText.get().isEmpty()) {
                disableButton.set(false);
                sendTextMessageChat(messageText.get().trim());
            } else {
                disableButton.set(true);
            }

        });
        binding.customBottombar.galleryImage.setOnClickListener(view -> {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (EasyPermissions.hasPermissions(requireActivity(), permission)) {
                selectAttachment(FileSelectionEnum.IMAGE);
            } else {
                showReadWritePermissionsRequiredDialog();
            }
        });
        binding.customBottombar.optionMenu.setOnClickListener(view1 -> {
            String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
            if (EasyPermissions.hasPermissions(requireActivity(), permission)) {
                new ChatAttachmentDialogFragment(new OnAttachmentClickListener() {
                    @Override
                    public void onClick(FileSelectionEnum fileSelectionEnum) {
                        selectAttachment(fileSelectionEnum);
                    }

                    @Override
                    public void onDocClick(FileSelectionEnum fileSelectionEnum) {
                        selectDocAttachment(fileSelectionEnum);
                    }
                }).show(
                        getChildFragmentManager(),
                        ChatAttachmentDialogFragment.TAG);
            } else {
                showReadWritePermissionsRequiredDialog();
            }
        });

    }

    private void setBinding() {
        if (getArguments() != null) {
            viewModel.getAppManager().groupModel = (GroupModel) getArguments().getSerializable("groupModel");
            viewModel.getGroupName();
        }
        binding.setDisableButton(disableButton);
        binding.setMessageText(messageText);
        binding.setShowTypingText(showTypingText);
        binding.setTypingUserName(typingText);
        binding.setGroupTitle(viewModel.groupTitle);


    }


    private void selectDocAttachment(FileSelectionEnum fileSelectionEnum) {
        ChatFileUtils.INSTANCE.file = null;
        if(fileSelectionEnum == FileSelectionEnum.DOC) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType(fileSelectionEnum.value);
                String[] mimeTypes = docMimeType;
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startForResultDocument.launch(intent);
            }else {
            CallParams value = viewModel.getAppManager().getSession();
            if (value !=  null) {
                    if (value.getMediaType() == MediaType.VIDEO)
                    { Toast.makeText(requireContext(), "Camera cannot be used", Toast.LENGTH_SHORT).show();
                    } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startForResultCamera.launch(intent);
                    }
                }
        else {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startForResultCamera.launch(intent);
            }
        }


    }

    ActivityResultLauncher<Intent> startForResultCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        try {
                            handleIntentDataCamera(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void checkForStoragePermissions(
            String msgId,
            HeaderModel headerModel,
            byte[] byteArray
    ) throws IOException {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
        ) {
            new StoragePermissionDialogFragment(msgId, headerModel, byteArray, new OnStoragePermissionListener() {
                @Override
                public void saveFile(String msgId, HeaderModel headerModel, byte[] byteArray) throws IOException {
                    saveFileMessage(msgId, headerModel, byteArray);
                }

                @Override
                public void sendTextMessage(String message) {
                    sendTextMessageChat(message);
                }

            }).show(
                    getChildFragmentManager(),
                    StoragePermissionDialogFragment.TAG);

        } else {
            saveFileMessage(msgId,headerModel,byteArray);
        }

    }

    public void saveFileMessage(String msgId, HeaderModel headerModel, byte[] byteArray) throws IOException {
        ChatFileUtils.INSTANCE.checkAndroidVersionToSave(requireContext(), headerModel, byteArray, new OnCompleteActionListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete() {
                ChatFileUtils.INSTANCE.sendAttachmentMessage(
                        headerModel,
                        ChatFileUtils.INSTANCE.file,
                        msgId,
                        viewModel.getAppManager().groupModel, viewModel.getAppManager(),
                        (OnNewMessageListener) message -> requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (message.getKey().equals(viewModel.getAppManager().groupModel.getChannelKey())) {
                                    AppManager.mapUnreadCount.clear();
                                    viewModel.usersList.clear();
                                    binding.progressBar.setVisibility(View.GONE);
                                    adapter.addItem(message);
                                    scrollToLast();
                                    viewModel.sendAcknowledgeMsgToGroup(message);
                                } else {
                                    viewModel.getAppManager().messageUpdateLiveData.postValue(message);
                                }
                            }
                        })
                );
            }
        });
    }

    private void handleIntentDataCamera(Intent data) throws IOException {
        if (data == null) {
            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            byteArray = ImageUtils.INSTANCE.convertBitmapToByteArray(
                    (Bitmap) data.getExtras().get("data"));

            ChatFileUtils.INSTANCE.saveImage(requireContext(),
                    byteArray,
                    String.valueOf(System.currentTimeMillis()),
                    "image/jpeg",
                    Environment.DIRECTORY_PICTURES + "/" + directoryName,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null
            );
        } else {
            ChatFileUtils.INSTANCE.file = FileUtils.INSTANCE.getFileData(
                    requireContext(),
                    ChatFileUtils.INSTANCE.getImageUri(
                            requireActivity(),
                            (Bitmap) data.getExtras().get("data")),
                   com.vdotok.connect.models.MediaType.IMAGE
            );

        }
        selectedFileCamera = ChatFileUtils.INSTANCE.file;
        fileType = 0;
        viewModel.getAppManager().getChatManager().sendFileToGroup(
                viewModel.getAppManager().groupModel.getChannelKey(),
                viewModel.getAppManager().groupModel.getChannelName(),
                ChatFileUtils.INSTANCE.file,
                fileType
        );
    }

    ActivityResultLauncher<Intent> startForResultDocument = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        try {
                            handleIntentData(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void selectAttachment(FileSelectionEnum fileSelectionEnum) {
        ChatFileUtils.INSTANCE.file = null;
        Uri uri;
        if (fileSelectionEnum == FileSelectionEnum.IMAGE) {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        } else if (fileSelectionEnum == FileSelectionEnum.AUDIO) {
            uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        } else {
            uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT, uri);
            intent.setType(fileSelectionEnum.value);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startForResult.launch(intent);
        }
    }

    /**
     * this is used to get Result respective of which type of attachment is selected i-e video,audio,and image from gallery
     */
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        try {
                            handleIntentData(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    private void handleIntentData(Intent data) throws IOException {
        if (data == null) {
            binding.progressBar.setVisibility(View.GONE);
        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        String mimeType = FileUtils.INSTANCE.getMimeType(requireContext(), data.getData());
        if (MimeTypeEnum.IMAGE.value.equals(mimeType.substring(0, mimeType.indexOf("/")))) {
            String pathDirectory = Environment.DIRECTORY_PICTURES;
            Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            com.vdotok.connect.models.MediaType mediaType = com.vdotok.connect.models.MediaType.IMAGE;
            fileType = 0;
            handleDataFromSelection(
                    data,
                    mimeType,
                    pathDirectory,
                    mediaUri,
                    mediaType,
                    fileType
            );
        } else if (MimeTypeEnum.VIDEO.value.equals(mimeType.substring(0, mimeType.indexOf("/")))) {
            String pathDirectory = Environment.DIRECTORY_MOVIES;
            Uri mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            com.vdotok.connect.models.MediaType mediaType = com.vdotok.connect.models.MediaType.VIDEO;
            fileType = 2;
            handleDataFromSelection(
                    data,
                    mimeType,
                    pathDirectory,
                    mediaUri,
                    mediaType,
                    fileType
            );
        } else if (MimeTypeEnum.AUDIO.value.equals(mimeType.substring(0, mimeType.indexOf("/")))) {
            String pathDirectory = Environment.DIRECTORY_MUSIC;
            Uri mediaUri =  MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            com.vdotok.connect.models.MediaType mediaType = com.vdotok.connect.models.MediaType.AUDIO;
            fileType = 1;
            handleDataFromSelection(
                    data,
                    mimeType,
                    pathDirectory,
                    mediaUri,
                    mediaType,
                    fileType
            );
        } else if (MimeTypeEnum.DOCTEXT.value.equals(mimeType.substring(0, mimeType.indexOf("/")))) {
            String pathDirectory = Environment.DIRECTORY_DOCUMENTS;
            Uri mediaUri = MediaStore.Files.getContentUri("external");
            com.vdotok.connect.models.MediaType mediaType = com.vdotok.connect.models.MediaType.FILE;
            fileType = 3;
            handleDataFromSelection(
                    data,
                    mimeType,
                    pathDirectory,
                    mediaUri,
                    mediaType,
                    fileType
            );
        } else if (MimeTypeEnum.DOC.value.equals(mimeType.substring(0, mimeType.indexOf("/")))) {
            String pathDirectory = Environment.DIRECTORY_DOCUMENTS;
            Uri mediaUri = MediaStore.Files.getContentUri("external");
            com.vdotok.connect.models.MediaType mediaType = com.vdotok.connect.models.MediaType.FILE;
            fileType = 3;
            handleDataFromSelection(
                    data,
                    mimeType,
                    pathDirectory,
                    mediaUri,
                    mediaType,
                    fileType
            );
        }
        if (ChatFileUtils.INSTANCE.file != null) {
            if (ChatFileUtils.INSTANCE.file.length() > FILE_SIZE_LIMIT) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(
                        requireContext(),
                        "File size should be less than 6MB",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                viewModel.getAppManager().getChatManager().sendFileToGroup(
                        viewModel.getAppManager().groupModel.getChannelKey(),
                        viewModel.getAppManager().groupModel.getChannelName(),
                        ChatFileUtils.INSTANCE.file,
                        fileType
                );
            }
        }

    }

    private void handleDataFromSelection(Intent data, String mimeType, String pathDirectory, Uri mediaUri, com.vdotok.connect.models.MediaType mediaType, int fileType) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (MimeTypeEnum.IMAGE.value.equals(mimeType.substring(0, mimeType.indexOf("/")))) {
                byteArray = ImageUtils.INSTANCE.convertImageToByte(requireContext(), Uri.parse(data.getData().toString()));
            } else if (MimeTypeEnum.VIDEO.value.equals(mimeType.substring(0, mimeType.indexOf("/")))) {
                String videoPath = ImageUtils.INSTANCE.copyFileToInternalStorage(requireContext(), data.getData(), "video");
                byteArray = FileUtils.INSTANCE.convertFileToByteArray(videoPath);
            } else if (MimeTypeEnum.AUDIO.value.equals(mimeType.substring(0, mimeType.indexOf("/")))) {
                String audioPath = ImageUtils.INSTANCE.copyFileToInternalStorage(requireContext(), data.getData(), "audio");
                byteArray = FileUtils.INSTANCE.convertFileToByteArray(audioPath);
            } else if (MimeTypeEnum.DOC.value.equals(mimeType.substring(0, mimeType.indexOf("/")))) {
                String filePath = ImageUtils.INSTANCE.copyFileToInternalStorage(requireContext(), data.getData(), "document");
                byteArray = FileUtils.INSTANCE.convertFileToByteArray(filePath);
            } else if (MimeTypeEnum.DOCTEXT.value.equals(mimeType.substring(0, mimeType.indexOf("/")))) {
                String filePath = ImageUtils.INSTANCE.copyFileToInternalStorage(requireContext(), data.getData(), "document");
                byteArray = FileUtils.INSTANCE.convertFileToByteArray(filePath);
            }
            ChatFileUtils.INSTANCE.saveImage(
                    requireContext(),
                    byteArray,
                    String.valueOf(System.currentTimeMillis()),
                    mimeType,
                    pathDirectory + "/" + directoryName,
                    mediaUri, null);
        } else {
            ChatFileUtils.INSTANCE.file = FileUtils.INSTANCE.getFileData(requireActivity(), data.getData(), mediaType);
        }
        selectedFile = ChatFileUtils.INSTANCE.file;
        this.fileType = fileType;
    }


    private void onNewMessage(Message message) {
        requireActivity().runOnUiThread(() -> {
            if (message.getKey().equals(viewModel.getAppManager().groupModel.getChannelKey())) {
                AppManager.mapUnreadCount.clear();
                viewModel.usersList.clear();
                binding.progressBar.setVisibility(View.GONE);
                adapter.addItem(message);
                scrollToLast();
                viewModel.sendAcknowledgeMsgToGroup(message);
            } else {
                viewModel.getAppManager().messageUpdateLiveData.postValue(message);
            }
        });
    }

    private void sendTypingMessage(String refId, boolean isTyping) {
        if (viewModel.getAppManager().groupModel != null) {
            String content = isTyping ? TYPING_START : TYPING_STOP;
            Message chatModel = new Message(
                    String.valueOf(System.currentTimeMillis()),
                    viewModel.getAppManager().groupModel.getChannelName(),
                    viewModel.getAppManager().groupModel.getChannelKey(),
                    refId,
                    MessageType.typing,
                    content,
                    0f,
                    viewModel.getAppManager().groupModel.getParticipants().size() > 1,
                    0,
                    0,
                    System.currentTimeMillis(),
                    0f,
                    0

            );
            viewModel.getAppManager().getChatManager().sendTypingMessage(chatModel, chatModel.getKey(), chatModel.getTo());
        }
    }


    private void sendTextMessageChat(String message) {
        binding.progressBar.setVisibility(View.VISIBLE);
        Message chatModel = null;
        if (viewModel.getAppManager().groupModel != null) {
            if (viewModel.getAppManager().getUserData().getRefId() != null) {
                chatModel = new Message(
                        String.valueOf(System.currentTimeMillis()),
                        viewModel.getAppManager().groupModel.getChannelName(),
                        viewModel.getAppManager().groupModel.getChannelKey(),
                        viewModel.getAppManager().getUserData().getRefId(),
                        MessageType.text,
                        message.trim(),
                        0f,
                        viewModel.getAppManager().groupModel.getParticipants().size() > 1,
                        ReceiptType.SENT.INSTANCE.getValue(),
                        0,
                        System.currentTimeMillis(),
                        0f,
                        0

                );
            }
            if (chatModel != null) {
                viewModel.getAppManager().getChatManager().publishMessage(chatModel);
            }
            messageText.set("");
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        showReadWritePermissionsRequiredDialog();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showOnTypingMessage(Message message) {
        if (message.getContent().equals(TYPING_START)) {
            message.setFrom(viewModel.getUserName(message));
            showTypingText.set(true);
            typingText.set(viewModel.getNameOfUsers(message));
            hideTypingText();
        }
    }


    /**
     * this method is used to hide typing status through binding after interval of 2 sec
     */

    private void hideTypingText() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                showTypingText.set(false);
            }

        }, 2000);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onFileSend(String fileHeaderId, int fileType) {
        super.onFileSend(fileHeaderId, fileType);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.sendStatus = true;
                if (fileType == com.vdotok.connect.models.MediaType.IMAGE.getValue()) {
                    adapter.addItem(
                            ChatFileUtils.INSTANCE.makeImageItemModel(
                                    ChatFileUtils.INSTANCE.file,
                                    viewModel.getDummyHeader(fileType),
                                    viewModel.getAppManager().groupModel,
                                    fileHeaderId
                            )
                    );
                    viewModel.getAppManager().updateMessageMapData(
                            ChatFileUtils.INSTANCE.makeImageItemModel(
                                    ChatFileUtils.INSTANCE.file,
                                    viewModel.getDummyHeader(fileType),
                                    viewModel.getAppManager().groupModel,
                                    fileHeaderId
                            )
                    );
                } else {
                    adapter.addItem(
                            ChatFileUtils.makeFileItemModel(
                                    ChatFileUtils.INSTANCE.file,
                                    viewModel.getDummyHeader(fileType),
                                    viewModel.getAppManager().groupModel,
                                    fileHeaderId
                            )
                    );
                    viewModel.getAppManager().updateMessageMapData(
                            ChatFileUtils.INSTANCE.makeFileItemModel(
                                    ChatFileUtils.INSTANCE.file,
                                    viewModel.getDummyHeader(fileType),
                                    viewModel.getAppManager().groupModel,
                                    fileHeaderId
                            )
                    );
                }
                scrollToLast();
            }
        });
    }

    @Override
    public void onFileReceivedCompleted(HeaderModel headerModel, byte[] byteArray, String msgId) throws IOException {
        super.onFileReceivedCompleted(headerModel, byteArray, msgId);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    checkForStoragePermissions(msgId, headerModel, byteArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onReceiptReceive(ReadReceiptModel model) {
        super.onReceiptReceive(model);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ((model.getFrom() != viewModel.getAppManager().getUserData().getRefId())) {
                    adapter.updateMessageForReceipt(model);
                }
            }
        });
    }

    @Override
    public void onByteReceived(byte[] payload) {
        super.onByteReceived(payload);
        if (payload != null) {
            Message model = new Message(
                    String.valueOf(System.currentTimeMillis()),
                    viewModel.getAppManager().groupModel.getChannelName(),
                    viewModel.getAppManager().groupModel.getChannelKey(),
                    viewModel.getAppManager().getUserData().getRefId(),
                    MessageType.media,
                    ImageUtils.INSTANCE.encodeToBase64(payload),
                    0f,
                    viewModel.getAppManager().groupModel.getParticipants().size() > 1,
                    0, 0, System.currentTimeMillis(), 0f, 0
            );
            if (model != null) {
                adapter.addItem(model);
            }
        }
    }

    @Override
    public void onFileReceiveFailed() {
        super.onFileReceiveFailed();
        viewExtension.INSTANCE.showSnackBar(binding.getRoot(), getString(R.string.attachment_failed));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onMessageArrive(Message message) {
        super.onMessageArrive(message);
        viewModel.getAppManager().lastMessageGroupKey = message.getKey();
        if (message.getFrom() != viewModel.getOwnRefID()) {
            if (message.getTo() != null) {
                viewModel.getAppManager().mapUnreadCount.put(message.getTo(), viewModel.getAppManager().mapUnreadCount.containsKey(message.getTo()) ? viewModel.getAppManager().mapUnreadCount.get(message.getTo()) + 1 : 1);
            }
        }
        onNewMessage(message);
        viewModel.getAppManager().updateMessageMapData(message);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onTypingMessage(Message message) {
        super.onTypingMessage(message);
        if (message.getKey().equals(viewModel.getAppManager().groupModel.getChannelKey()) && !(message.getFrom().equals(viewModel.getAppManager().getUserData().getRefId()))) {
            showOnTypingMessage(message);
        }

    }

    @Override
    public void onAttachmentProgressSend(String fileHeaderId, int progress) {
        super.onAttachmentProgressSend(fileHeaderId, progress);
        if (progress == 100) {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (Message msg : adapter.dataModelList) {
                        if (msg.getId().equals(fileHeaderId)) {
                            int index = adapter.dataModelList.indexOf(msg);
                            adapter.sendStatus = false;
                            adapter.notifyItemChanged(index);
                        }
                    }
                }
            });
        }
    }

}

