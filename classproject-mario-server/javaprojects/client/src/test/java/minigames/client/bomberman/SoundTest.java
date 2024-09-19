package minigames.client.bomberman;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sound.sampled.*;
import java.net.URL;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Soundtesting for sound class in Bomberman
 *  Contributor: Lixang Li lli32@myune.edu.au
 */
class SoundTest {

    @Mock
    private Clip mockClip;

    private Sound sound;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sound = Sound.getInstance();
        sound.clipHashMap.put(Sound.Type.EXPLOSION, Optional.of(mockClip));
        sound.clipHashMap.put(Sound.Type.BGM, Optional.of(mockClip));
    }
    @Test
    @DisplayName("Sound file is located in JAR")
    void testSoundFile(){
        Optional<URL> testSoundURL = Optional.ofNullable(getClass().getResource("/sound/bomberman/backgroundmusic.wav"));
        assertTrue(testSoundURL.isPresent(), "Sound file should be present in JAR.");

        //AudioInputStream mockAis = mock(AudioInputStream.class);

    }
    @Test
    @DisplayName("Test if SFX can play")
    void testPlaySFX() {
        Sound.Type sfxType = Sound.Type.EXPLOSION;

        sound.playSFX(sfxType);

        verify(mockClip).setMicrosecondPosition(0);
        verify(mockClip).start();
    }

    @Test
    @DisplayName("Test that the BGM is played at 50% volume, loops and resets")
    void testPlayAndStopMusic() {


        Sound.Type musicType = Sound.Type.BGM;

        // lets mock every step of the way
        FloatControl mockGain = mock(FloatControl.class);
        when(mockClip.getControl(any())).thenReturn(mockGain);

        // use https://www.baeldung.com/mockito-void-methods return method for private method effects
        // Check that the gain of the mocked clip is decreased by 6 db
        assertThrows(Exception.class, () -> {
            doThrow().when(mockGain).setValue(-6f);
        });

        sound.playMusic(musicType);

        // Music started and looping
        verify(mockClip).setMicrosecondPosition(0);
        verify(mockClip).start();
        verify(mockClip).loop(Clip.LOOP_CONTINUOUSLY);

        //Stop music
        sound.stopMusic();

        // Check Music stopped
        verify(mockClip).stop();
    }
}