package de.stephanlindauer.criticalmaps.adapter;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.stephanlindauer.criticalmaps.R;
import de.stephanlindauer.criticalmaps.databinding.ViewChatmessageBinding;
import de.stephanlindauer.criticalmaps.interfaces.IChatMessage;
import de.stephanlindauer.criticalmaps.model.chat.ReceivedChatMessage;
import de.stephanlindauer.criticalmaps.utils.TimeToWordStringConverter;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder> {

    private List<IChatMessage> chatMessages;

    static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        private final ViewChatmessageBinding binding;
        private final DateFormat dateFormatter = DateFormat.getDateTimeInstance(
                DateFormat.DEFAULT, DateFormat.SHORT, Locale.getDefault());
        private ObjectAnimator sendingAnimator;

        ChatMessageViewHolder(ViewChatmessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(IChatMessage message) {
            binding.chatmessageMessageText.setText(message.getMessage());
            if (message instanceof ReceivedChatMessage) {
                dateFormatter.setTimeZone(TimeZone.getDefault());
                binding.chatmessageLabelText.setText(TimeToWordStringConverter.getTimeAgo(
                        ((ReceivedChatMessage) message).getTimestamp(), itemView.getContext()));
            } else {
                binding.chatmessageLabelText.setText(R.string.chat_sending);

                sendingAnimator = (ObjectAnimator) AnimatorInflater.loadAnimator(
                        itemView.getContext(), R.animator.map_gps_fab_searching_animation);
                sendingAnimator.setTarget(binding.chatmessageLabelText);
                sendingAnimator.start();
            }
        }

        void clearAnimation() {
            if (sendingAnimator != null) {
                sendingAnimator.cancel();
                binding.chatmessageLabelText.setAlpha(1f);
            }
        }
    }

    public ChatMessageAdapter(List<IChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final ViewChatmessageBinding binding =
                ViewChatmessageBinding.inflate(inflater, parent, false);

        return new ChatMessageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        holder.bind(chatMessages.get(position));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull ChatMessageViewHolder holder) {
        holder.clearAnimation();
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void updateData(List<IChatMessage> savedAndOutgoingMessages) {
        this.chatMessages = savedAndOutgoingMessages;
        notifyDataSetChanged();
    }
}
