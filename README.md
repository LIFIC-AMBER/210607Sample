# Lific - 21.06.09 Amber 세미나 자료
날씨 API 를 이용한 샘플 앱.  
앞으로 앱 개발할 때 사용해보고 싶은 기술들을 간단하게 적용한 앱으로, 참고용으로만 봐주세요.


---

### 적용된 기술
1. Bottom Navigation View + Navigation Extensions 
2. Kotlin coroutine + flow + LiveData/StateFlow 
<pre>
  flow branch -> repository 함수들이 flow builder를 통해 api를 호출하여 값을 전달하도록 함.
  stateflow branch -> flow 브랜치에서 사용된 livedata 대신 stateflow 를 적용함.
</pre>
3. Motion Layout
4. 테스트코드(ViewModel) - motion 브랜치에서만 봐주세요.

### 사용한 라이브러리
* 이미지 로딩 : [Glide] (+ [svg] 참고)
* 네트워크 통신 : [Retrofit2]
* 테스트 : [Coroutine], [Mockito], [Mockito-kotlin], [LiveData]

[Glide]: https://github.com/bumptech/glide
[svg]: https://github.com/bumptech/glide/tree/master/samples/svg/src/main/java/com/bumptech/glide/samples
[Retrofit2]: https://square.github.io/retrofit/
[Coroutine]: https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/
[Mockito]: https://site.mockito.org/
[Mockito-kotlin]: https://github.com/mockito/mockito-kotlin
[LiveData]: https://developer.android.com/jetpack/androidx/releases/lifecycle
