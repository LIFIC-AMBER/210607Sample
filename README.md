# Lific - 21.06.09 Amber 세미나 자료
날씨 API 를 이용한 샘플 앱


![Screenshot](/screenshots/Screenshot_1.png)
![Screenshot](/screenshots/Screenshot_2.png)

---

### 적용된 기술
1. Bottom Navigation View + Navigation Extensions
2. Kotlin coroutine + flow + LiveData -> 추후 StateFlow 적용해보기
3. Motion Layout
4. 테스트코드(ViewModel)

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
