#Instantmessag
第一周周三
#MVP把包分清,哪部分是M,哪部分是V,哪部分是P
#Activity放到包里面
#activity_login里面,相同的EditText/Button等属性,整理到style.以后有相同的都必须整理
#activity_main同上
#????????styles里面username和password属性那么多都相同 为啥要写两个?

第一周周五
activity_login /activity_main 两个文件里面,相同属性太多,需要整合.上次已经检查出来了,为什么不整理.
包分的不对,包名写全了.mvp分包都写在外层,写到ui层里面是什么意思
外层包名跟内层有重复,改!
ChatModel类里面,去看我留的注释!
包名按照规定来取,自定义控件要么叫view,要么叫widget,是随便翻译一个就能放的么!

分包分包!!改!

2016-03-10日
utils包下不要有自定义的控件,换包
包分的太乱.类的功能分布不合理.例如DBHelper类里面还有Parse的方法,但是还有Parse这个类.
Toast工具类里面的Context写成application的Context,或者Context.getApplicationContext();否则容易内存泄露.

