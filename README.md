# CustomSmartwatch
A very simple project with a very uncreative name. 


The project consists of two parts. The android app that is supposed to send drawing commands, and the arm mcu that is attached to the screen and executes the commands by drawing. At the moment supports only text, but the possibility of sending images should be added. The android app uses NotificationListenerService to get the needed data that is impossible to get using other means. Now it supports only google maps, but support for other apps/services comming soon(tm). 

TODO:

- [ ] Support for sending and drawing images.
- [ ] Convering android Bitmaps to images that can be drawn on monochrome displays.
- [ ] Integrating with messeging/facebook/discord services.
- [ ] Paging(The ability to have multiple screens controled with buttons).
- [ ] Have fun.
