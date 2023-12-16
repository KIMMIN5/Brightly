밝게(Brightly) 앱

Description

밝게는 계명대학교 캠퍼스 내 안전과 편의를 증진하기 위해 개발된 지도 기반의 모바일 어플리케이션입니다. 이 앱은 사용자에게 캠퍼스의 가로등, 건물, 안전 시설의 위치와 상태 정보를 제공하며, 긴급 신고 기능을 포함하고 있습니다.

Environment

OS: Android
개발 환경: Android Studio
필요한 하드웨어: GPS 기능이 탑재된 스마트폰
최소 요구 사양: Android API Level 24
Prerequisite

Google Maps API: 지도 기능을 사용하기 위해 필요합니다.
Firebase: 데이터베이스 관리 및 실시간 데이터 동기화를 위해 필요합니다.
Files

MainActivity.java: 앱의 메인 활동을 제어하고, 사용자 인터페이스 및 지도 설정을 초기화합니다.
CurrentLocation.java: 사용자의 현재 위치를 추적하고 업데이트합니다.
EventOfLamp.java: 가로등 마커 클릭 이벤트를 처리합니다.
EventOfBuilding.java: 건물 마커 클릭 이벤트를 처리합니다.
LampManager.java: 가로등 정보를 관리하고 지도에 표시합니다.
BuildingManager.java: 건물 정보를 관리하고 지도에 표시합니다.
Permissions.java: 필요한 권한을 요청하고 처리합니다.
Usage

앱을 설치하고 실행합니다.
필요한 권한을 승인합니다.
앱의 메인 화면에서 캠퍼스 지도를 볼 수 있습니다.
가로등이나 건물의 마커를 클릭하여 상세 정보를 확인할 수 있습니다.
긴급 신고 버튼을 사용하여 신고를 할 수 있습니다.
