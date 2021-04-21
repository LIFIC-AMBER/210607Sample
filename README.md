# recruit-backpacker
백패커 채용 과제 - 날씨 API 를 이용하여 샘플과 같은 앱 개발

![Screenshot](/screenshots/Screenshot_1.png)]
![Screenshot](/screenshots/Screenshot_2.png)]
![Screenshot](/screenshots/Screenshot_3.png)]
![Screenshot](/screenshots/Screenshot_4.png)]

---

### 과제 진행 과정
1. 요구 사항 확인
    <pre>- 앱을 켜면 날씨 데이터를 불러와 테이블 형태로 표출
   - swipe refresh로 데이터를 새로 불러오도록 함.</pre>
2. API 확인 및 관련 데이터 구조 정의 
    <pre>- DTO 및 화면 표출용 데이터 구조 정의</pre>
3. UI 개발
4. API 연동
    <pre>- 지역명 검색을 통해 얻어진 결과를 이용하여 날씨 데이터를 불러옴. </pre>
5. 예외 처리
    <pre>- 통신 실패는 Toast 메세지로 처리
   - 버그 수정</pre>
6. 테스트코드 작성

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
