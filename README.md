# 밝게(Brightly) 앱

----
## 📖Description

#### 밝게는 계명대학교 캠퍼스 내 안전과 편의를 증진하기 위해 개발된 지도 기반의 모바일 어플리케이션입니다. 이 앱은 사용자에게 캠퍼스의 가로등, 건물, 안전 시설의 위치와 상태 정보를 제공하며, 긴급 신고 기능을 포함하고 있습니다.

----
## 📅Develop Period
#### 23.10.09 - 23.12.08

----
## 👥Develop Member
#### 이거팔아 100조(김민오, 김보빈, 오우빈, 최재영)

-----
## ⚙️Environment

* OS: Android
* 'Java 17.0.9'
* 'JDK 17'
* 개발 환경: Android Studio v11.0.5
* 필요한 하드웨어: GPS 기능이 탑재된 안드로이드 스마트폰
* 최소 요구 사양: Android API Level 24

----
## 🔧Tools

* AndroidStudio : 애플리케이션 제작 및 구현을 위해 필요
* Github : 팀원간 코드 합병 및 코드 복구를 위해 필요
* Notion : 일정 관리 및 스케쥴링을 위해 필요
* Google Maps API: 지도 기능을 사용하기 위해 필요
* Firebase: 데이터베이스 관리 및 실시간 데이터 동기화를 위해 필요

----
## 📜Usage(Android Studio)
1. App 단계의 폴더를 Android Studio로 열기
2. File-Sync_Project_With_Gradle_Files를 클릭하여 Gradle Sync 맞추기
3. Run-Run_'app'을 클릭하여 애플리케이션 실행

----
## 📜Usage(Android App)

1. 앱을 설치하고 실행합니다.
2. 필요한 권한을 승인합니다.
3. 앱의 메인 화면에서 캠퍼스 지도를 볼 수 있습니다.
4. 가로등이나 건물의 마커를 클릭하여 상세 정보를 확인할 수 있습니다.
5. 긴급 신고 버튼을 사용하여 신고를 할 수 있습니다.

----
## 📙Main Function

### 💡가로등

* 가로등 위치 확인
* 가로등 고장 정보 확인
* 가로등 고장 신고

### 🏢건물
* 건물 위치 확인
* 수위실 전화번호 확인
* 수위실 전화번호 원터치 전화
* 해당 건물 야간 수업 정보 확인

### 🚨긴급 신고
* 원터치 112 긴급 신고

----
## 📁Files

* MainActivity.java: 앱의 메인 활동을 제어하고, 사용자 인터페이스 및 지도 설정을 초기화
* AndroidManifest.xml: 애플리케이션의 메타데이터를 정의와 필요한 권한, 액티비티, 서비스 선언 등을 포함합니다.
* Layout files (XML): 사용자 인터페이스 레이아웃을 정의와 각 화면의 구성 요소 및 디자인을 XML 형식으로 구현합니다.
### Map Package
* CreateMap.java: 지도를 생성하고 초기 설정을 담당하고 지도의 초기 뷰, 경계 설정 등을 관리
* CurrentLocation.java: 사용자의 현재 위치를 추적하고, 지도에 현재 위치를 업데이트하는 기능 
* DayAndNight.java: 시간에 따라 지도의 스타일을 주간 또는 야간 모드로 전환하는 기능을 담당 
* LimitedBoundary.java: 지도의 이동 가능한 경계를 제한하는 기능을 제공
### User Package 
* EventOfLamp.java: 가로등 마커에 대한 사용자 인터랙션을 처리와 클릭 이벤트와 관련 UI 업데이트 
* EventOfBuilding.java: 건물 마커에 대한 사용자 인터랙션을 처리와 클릭 이벤트에 따른 정보 표시 기능 
* Permissions.java: 앱이 필요로 하는 권한을 사용자에게 요청하고, 이에 대한 응답을 처리 
### Admin Package
* LampManager.java: 가로등 관련 정보를 관리하고, 지도에 가로등 마커를 표시하는 역할 
* BuildingManager.java: 건물 관련 정보를 관리하고, 지도에 건물 마커를 표시
* DataFetcher.java: 외부 데이터 소스로부터 정보를 가져오는 기능을 담당합니다. 가로등 및 건물 정보를 수집 
* SaveMarker.java: 사용자가 지도에 추가한 마커의 위치 정보를 내부 저장소에 저장하고, 필요 시 불러오는 기능 
* SharedPreferencesExporter.java: 앱의 내부 저장 데이터(예: 사용자 설정, 마커 위치 등)를 외부 파일로 내보내는 기능
* ButtonOfCurrent.java: 현재 위치에 마커를 추가하는 버튼과 사용자의 현재 위치에 마커를 추가하고 저장하는 역할

----
