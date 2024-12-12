// record.js

class AudioRecorderJS {
    constructor() {
        this.mediaRecorder = null;
        this.audioChunks = [];
        this.audioUrl = null;
        this.stream = null;
    }

    async startRecording(mimeType) {
        try {
            this.stream = await navigator.mediaDevices.getUserMedia({ audio: true });
            const options = mimeType ? { mimeType } : {};
            this.mediaRecorder = new MediaRecorder(this.stream, options);

            this.audioChunks = []; // Clear previous recording data

            this.mediaRecorder.ondataavailable = (event) => {
                this.audioChunks.push(event.data);
            };

            this.mediaRecorder.onstop = () => {
                const audioBlob = new Blob(this.audioChunks, { type: this.mediaRecorder.mimeType });
                this.audioUrl = URL.createObjectURL(audioBlob);
                // You might want to dispatch a custom event or call a Kotlin callback here
                // to notify that recording has stopped and provide the URL
            };

            this.mediaRecorder.start();
            return true; // Indicate successful start
        } catch (error) {
            console.error("Error starting recording:", error);
            return false; // Indicate failure
        }
    }

    stopRecording() {
        if (this.mediaRecorder && this.mediaRecorder.state === "recording") {
            this.mediaRecorder.stop();
            this.stream.getTracks().forEach(track => track.stop());
            return this.audioUrl; // Return the URL of the recorded audio
        }
        return null;
    }

    isRecording() {
        return this.mediaRecorder?.state === "recording";
    }
}

// Export an instance to be used directly in Kotlin
const audioRecorderJS = new AudioRecorderJS();
window.audioRecorderJS = audioRecorderJS;