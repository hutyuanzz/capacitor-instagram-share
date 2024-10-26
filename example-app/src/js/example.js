import { ShareInstagram } from '@capacitor-pay2stay&#x2F;instagram-share';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    ShareInstagram.echo({ value: inputValue })
}
