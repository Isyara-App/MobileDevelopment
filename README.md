# Information About This Project [Mobile Development]

## Description
Isyara application focuses on sign language translation along with learning for people who are new to sign language accompanied by a word dictionary and letter dictionary and quiz to improve knowledge.

## Preview



## Architecture
```
C:.
|   AndroidManifest.xml
|   ic_launcher-playstore.png
|
+---java
|   \---com
|       \---example
|           \---isyara
|               |   MainActivity.kt
|               |   TranslateFragment.kt
|               |
|               +---data
|               |   |   Result.kt
|               |   |
|               |   +---pref
|               |   |       UserPreferences.kt
|               |   |
|               |   +---remote
|               |   |   +---response
|               |   |   |       CheckAnswerResponse.kt
|               |   |   |       CheckCompletionResponse.kt
|               |   |   |       CommunityResponse.kt
|               |   |   |       ContactResponse.kt
|               |   |   |       DeleteImageProfileResponse.kt
|               |   |   |       DictionarySentenceResponse.kt
|               |   |   |       DictionaryWordResponse.kt
|               |   |   |       EventResponse.kt
|               |   |   |       LoginResponse.kt
|               |   |   |       LogoutResponse.kt
|               |   |   |       NewsResponse.kt
|               |   |   |       ProfileResponse.kt
|               |   |   |       QuestionResponse.kt
|               |   |   |       QuizByIdResponse.kt
|               |   |   |       QuizResponse.kt
|               |   |   |       RegisterResponse.kt
|               |   |   |
|               |   |   \---retrofit
|               |   |           ApiConfig.kt
|               |   |           ApiService.kt
|               |   |
|               |   \---repository
|               |           AuthRepository.kt
|               |           ContactRepository.kt
|               |           DictionaryRepository.kt
|               |           InformationRepository.kt
|               |           QuizRepository.kt
|               |           UserRepository.kt
|               |
|               +---di
|               |       Injection.kt
|               |
|               +---ui
|               |   +---dictionary
|               |   |       DictionaryFragment.kt
|               |   |       DictionaryViewModel.kt
|               |   |
|               |   +---dictionary_sentence
|               |   |       DictionarySentenceAdapter.kt
|               |   |       DictionarySentenceFragment.kt
|               |   |       DictionarySentenceViewModel.kt
|               |   |       DictionarySentenceViewModelFactory.kt
|               |   |
|               |   +---dictionary_word
|               |   |       DictionaryWordAdapter.kt
|               |   |       DictionaryWordFragment.kt
|               |   |       DictionaryWordViewModel.kt
|               |   |       DictionaryWordViewModelFactory.kt
|               |   |
|               |   +---help
|               |   |       HelpFragment.kt
|               |   |       HelpViewModel.kt
|               |   |       HelpViewModelFactory.kt
|               |   |
|               |   +---homescreen
|               |   |       HomeScreenAdapter.kt
|               |   |       HomeScreenFragment.kt
|               |   |       HomeScreenViewModel.kt
|               |   |       HomeScreenViewModelFactory.kt
|               |   |
|               |   +---information
|               |   |       InformationAdapter.kt
|               |   |       InformationDetailFragment.kt
|               |   |       InformationFragment.kt
|               |   |       InformationViewModel.kt
|               |   |       InformationViewModelFactory.kt
|               |   |
|               |   +---information_event
|               |   |       EventInformationAdapter.kt
|               |   |       EventInformationFragment.kt
|               |   |       EventInformationViewModel.kt
|               |   |       EventInformationViewModelFactory.kt
|               |   |
|               |   +---loginandsignup
|               |   |       LoginFragment.kt
|               |   |       LoginViewModel.kt
|               |   |       LoginViewModelFactory.kt
|               |   |       SignUpFragment.kt
|               |   |       SignUpViewModel.kt
|               |   |       SignUpViewModelFactory.kt
|               |   |
|               |   +---news_detail
|               |   |       NewsDetailFragment.kt
|               |   |       NewsDetailViewModel.kt
|               |   |
|               |   +---onboard
|               |   |       OnboardFragment.kt
|               |   |       OnboardViewModel.kt
|               |   |
|               |   +---profile
|               |   |       ProfileFragment.kt
|               |   |       ProfileViewModel.kt
|               |   |       ProfileViewModelFactory.kt
|               |   |
|               |   +---quiz
|               |   |   |   QuizAdapter.kt
|               |   |   |   QuizFragment.kt
|               |   |   |   QuizViewModel.kt
|               |   |   |   QuizViewModelFactory.kt
|               |   |   |
|               |   |   +---ingame
|               |   |   |       InGameFragment.kt
|               |   |   |       InGameViewModel.kt
|               |   |   |       InGameViewModelFactory.kt
|               |   |   |
|               |   |   +---question
|               |   |   |       QuestionAdapter.kt
|               |   |   |       QuestionFragment.kt
|               |   |   |       QuestionViewModel.kt
|               |   |   |       QuestionViewModelFactory.kt
|               |   |   |
|               |   |   \---result
|               |   |       +---failed
|               |   |       |       FailedResultFragment.kt
|               |   |       |
|               |   |       \---pass
|               |   |               PassResultFragment.kt
|               |   |
|               |   +---settings
|               |   |       SettingsFragment.kt
|               |   |       SettingsViewModel.kt
|               |   |       SettingsViewModelFactory.kt
|               |   |
|               |   \---translate
|               |           TranslateFragment.kt
|               |           TranslateViewModel.kt
|               |
|               \---util
|                       ImageClassifierHelper.kt
|                       LoadImage.kt
|                       ObjectDetectorHelper.kt
|                       OverlayView.kt
|                       response.kt
|                       SafeApiCall.kt
|
+---ml
|       model.tflite
```

## Link

- Api   : Soon
- Figma : https://www.figma.com/design/iNjMEfcCCewqGRYpL5j1MT/app-prototype?node-id=2027-931

## TODO Tasks

### Week 1

- [x] Onboard Screen
- [x] Login
- [x] Sign up
- [x] Home Screen
- [x] Detail News

### Week 2

- [x] Setting
- [x] Contact Us
- [x] Profile
- [x] Kamus
- [x] Kamus huruf
- [x] Kamus kata

### Week 3

- [x] Translate Isyarat (UI)
- [x] Implemented Model On Device
- [x] Information

### Week 4

- [x] Splash Screen
- [x] Quiz
