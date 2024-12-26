// record.js

// record.js
class AudioRecorderJS {
    constructor() {
        this.mediaRecorder = null;
        this.audioChunks = [];
        this.audioUrl = null;
        this.stream = null;
    }

    async startRecording() {
        try {
            // Check microphone permission before requesting it
            const permissionStatus = await navigator.permissions.query({ name: 'microphone' });

            if (permissionStatus.state === 'denied') {
                console.error("Microphone permission denied.");
                window.dispatchEvent(new CustomEvent('audioRecordingPermissionDenied'));
                return false;
            }

            if (permissionStatus.state === 'prompt' || permissionStatus.state === 'granted') {
                this.stream = await navigator.mediaDevices.getUserMedia({ audio: true });
            }

            const options = { mimeType: 'audio/webm;codecs=opus' }; // Supported MIME type
            this.mediaRecorder = new MediaRecorder(this.stream, options);

            this.audioChunks = []; // Clear previous recording data

            this.mediaRecorder.ondataavailable = (event) => {
                if (event.data.size > 0) {
                    this.audioChunks.push(event.data);
                } else {
                    console.warn("Empty data chunk received.");
                }
            };

            this.mediaRecorder.onstop = () => {
                const audioBlob = new Blob(this.audioChunks, { type: 'audio/webm' });
                console.log("Blob size:", audioBlob.size);

                if (audioBlob.size === 0) {
                    console.error("Recording failed: Empty Blob.");
                    window.dispatchEvent(new CustomEvent('audioRecordingError'));
                    return;
                }

                this.audioUrl = URL.createObjectURL(audioBlob);

                // Dispatch a custom event with the Blob URL
                window.dispatchEvent(new CustomEvent('audioRecordingStopped', { detail: this.audioUrl }));
            };

            this.mediaRecorder.start();
            console.log("Recording started...");
            return true; // Indicate successful start
        } catch (error) {
            console.error("Error starting recording:", error);
            window.dispatchEvent(new CustomEvent('audioRecordingError', { detail: error.message }));
            return false; // Indicate failure
        }
    }

    async stopRecording() {
        if (this.mediaRecorder && this.mediaRecorder.state === "recording") {
            console.log("Stopping recording...");
            this.mediaRecorder.stop(); // Stop the MediaRecorder
            this.stream.getTracks().forEach(track => track.stop()); // Stop all media tracks

            return new Promise((resolve, reject) => {
                this.mediaRecorder.addEventListener("stop", () => {
                    try {
                        // Combine recorded chunks into a Blob
                        const audioBlob = new Blob(this.audioChunks, { type: "audio/webm" });
                        console.log("Blob size:", audioBlob.size);

                        if (audioBlob.size === 0) {
                            console.error("Error: Blob is empty.");
                            reject("Recording failed: Blob is empty.");
                            return;
                        }

                        const audioUrl = URL.createObjectURL(audioBlob); // Generate Blob URL
                        console.log("Generated Blob URL:", audioUrl);

//                        const audio = new Audio(audioUrl); // Create an audio element
//                        audio.play()
//                            .then(() => console.log("Audio playback started"))
//                            .catch((error) => console.error("Audio playback error:", error));

                        resolve(audioUrl); // Resolve the promise with the Blob URL
                    } catch (error) {
                        console.error("Error processing recording:", error);
                        reject(error);
                    }
                }, { once: true });
            });
        } else {
            console.warn("No active recording to stop.");
            return Promise.resolve(null); // Return null if no recording is active
        }
    }


  consoleLog(message) {
        console.log(message);
    }


    isRecording() {
        return this.mediaRecorder?.state === "recording";
    }
}

// Export an instance to be used directly in Kotlin
const audioRecorderJS = new AudioRecorderJS();
window.audioRecorderJS = audioRecorderJS;