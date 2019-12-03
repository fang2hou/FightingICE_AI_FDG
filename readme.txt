已经打包好了，分两块

1. TTS Bridge
// 导入
import ice_agent.TTSBridge;

// 生成一个对象
TTSBridge tts = new TTSBridge();

// 改变对象的属性
// 可以改变的参数（只要进行字符串赋值即可，内部会自动转换）
// voice_name; text; language_code; gain; rate; pitch
// 比如要改成一个女声
tts.voice_name = "en-US-Wavenet-A";

// 说话
tts.speak("Hello, I am Tony, and I will be your guide in the following games, nice to meet you!");

2. UKI Bridge
// 导入
import ice_agent.UKIBridge;

// 生成一个对象
UKIBridge uki = new UKIBridge();

// 读取 返回字符串，失败为 null
String result = uki.read();

// 传送 返回布尔值，失败为false，成功为true
boolean result = uki.save("data");