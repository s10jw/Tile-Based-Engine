package byow.Core;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Sound {
    Clip clip;
    AudioInputStream stream;
    DataLine.Info info;
    AudioFormat format;
    File file;
    int framePos = 0;

    public Sound(String filePath) {
        try {
            file = new File(filePath);
            stream = AudioSystem.getAudioInputStream(file);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip =  (Clip) AudioSystem.getLine(info);
            clip.open(stream);
        } catch(Exception e) {
            System.out.println("Music file not found.");
        }
    }

    public void play() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }
    public void stop() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.stop();
        }
    }

    public void pause() {
        if (clip != null) {
            framePos = clip.getFramePosition();
            clip.stop();
        }
    }

    public void resume() {
        if (clip != null) {
            clip.setFramePosition(framePos);
            clip.start();
        }
    }

    public void setFramePos(int pos) {
        if (clip != null) {
            clip.setFramePosition(pos);
        }
    }

    public boolean isRunning() {
        if (clip != null) {
            return clip.isRunning();
        }
        return false;
    }

    public void loop() {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
}
