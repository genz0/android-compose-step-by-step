package com.example.practicecameraapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.practicecameraapp.ui.theme.PracticeCameraAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      PracticeCameraAppTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          // カメラパーミッションの許可を確認
          ConfirmPermission(modifier = Modifier.padding(innerPadding)) {
            Main(modifier = Modifier.padding(innerPadding))
          }
        }
      }
    }
  } // onCreateの閉じカッコ
} // MainActivityの閉じカッコ

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ConfirmPermission(
  modifier: Modifier,
  content: @Composable () -> Unit
) {
  // カメラパーミッションの状態を覚えておくためのオブジェクト
  val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

  // パーミッションが許可されていなければリクエストを発行する
  LaunchedEffect(Unit) {
    if (!cameraPermission.status.isGranted) {
      cameraPermission.launchPermissionRequest()
    }
  }

  // パーミッションの状態に応じて表示する画面を切り替える
  if (cameraPermission.status.isGranted) {
    // パーミッションが許可されていれば、Mainを表示
    content()
  } else {
    // 許可されていない場合、メッセージを表示
    Box(
      modifier = modifier.fillMaxSize(),
      contentAlignment = Alignment.Center
    ) {
      Text("カメラの使用を許可してください")
    }
  }
} // ConfirmPermissionの閉じカッコ

@Composable
fun Main(modifier: Modifier = Modifier) {
  val nav = rememberNavController()   // NavHostの状態管理
  // 撮影したBitmap画像を保持する状態変数
  var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }

  // NavHostで画面遷移を管理
  NavHost(navController = nav, startDestination = "preview", modifier = modifier) {
    composable("preview") { // カメラプレビュー画面
      CameraPreview(Modifier.fillMaxSize()) { bmp ->
        capturedBitmap = bmp // Bitmapを更新
        nav.navigate("recognize")
      }
    }
    composable("recognize") {// 画像認識画面
      RecognizeView(
        modifier = Modifier.fillMaxSize(),
        bitmap = capturedBitmap!!
      ) {
        nav.popBackStack()
      }
    }
  } // NavHostの閉じカッコ
} // Mainの閉じカッコ

// カメラプレビュー
@Composable
fun CameraPreview(
  modifier: Modifier,
  onCapture: (Bitmap) -> Unit
) {
  val context = LocalContext.current
  // CameraXのコントローラを生成して設定
  val cameraController = remember {
    LifecycleCameraController(context).apply {
      // 静止画撮影を有効化
      setEnabledUseCases(LifecycleCameraController.IMAGE_CAPTURE)
      // バックカメラを選択
      cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
      // 低遅延モードを設定
      imageCaptureMode = ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
    }
  }

  // コントローラをライフサイクルにバインド
  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(lifecycleOwner) {
    cameraController.bindToLifecycle(lifecycleOwner)
  }

  Box(modifier = modifier.fillMaxSize()) {
    // 画面全体にカメラプレビュー表示
    AndroidView(
      factory = { context ->
        PreviewView(context).apply {
          // 互換モードに設定して端末差を吸収
          implementationMode = PreviewView.ImplementationMode.COMPATIBLE
          // PreviewViewにコントローラを設定
          controller = cameraController
        }
      },
      modifier = Modifier.fillMaxSize(),
    )
    Button( // 撮影ボタン
      onClick = {
        cameraController.takePicture( // スナップショット撮影
          ContextCompat.getMainExecutor(context),
          object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) { // 変換成功
              val bitmap = image.use {
                // ImageProxyからBitmapへの変換
                val bmp = imageProxyToBitmap(it)
                // 回転している場合の考慮
                rotateBitmapIfNeeded(bmp, it.imageInfo.rotationDegrees)
              }
              onCapture(bitmap) // 画像認識画面に表示する画像を渡す
            }

            override fun onError(exception: ImageCaptureException) { // 変換失敗
              // 失敗しても画像を返却する
              onCapture(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888))
            }
          }
        )
      },
      modifier = Modifier
        .align(Alignment.BottomCenter) //カメラプレビューに重ね、画面下部に表示
        .padding(16.dp)
    ) {
      Icon(imageVector = Icons.Filled.Camera, contentDescription = "撮影")
    }
  }
} // CameraPreviewの閉じカッコ

// ImageProxyからBitmapへの変換
private fun imageProxyToBitmap(image: ImageProxy): Bitmap {
  // ImageProxyからByteBufferを取り出す
  val buf = image.planes.firstOrNull()?.buffer
    ?: return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
  // ByteバッファからByte配列に変換
  val bytes = ByteArray(buf.remaining()).also { buf.get(it) }
  // Bitmap生成
  return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
} // imageProxyToBitmapの閉じカッコ

// Bitmap回転角の考慮
private fun rotateBitmapIfNeeded(src: Bitmap, rotationDegrees: Int): Bitmap {
  // rotationDegreesはセンサー向きと端末回転から決まる。回転していなければそのまま返却
  if (rotationDegrees == 0) return src
  // 回転行列を生成し変換
  val m = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
  // Bitmapを生成する
  return Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true).also {
    // 変換したら元のBitmapを破棄する
    if (it != src) src.recycle()
  }
} // rotateBitmapIfNeededの閉じカッコ

// 画像認識画面
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecognizeView(
  modifier: Modifier,
  bitmap: Bitmap,
  onBack: () -> Unit,
) {
  // 加工・表示用Bitmap。初期値は撮影されたbitmap
  var editBitmap by remember { mutableStateOf(bitmap) }
  // ModalBottomSheetの状態管理
  val sheetState = rememberModalBottomSheetState()
  var showSheet by remember { mutableStateOf(false) }
  // 認識結果のリスト
  var textList by remember { mutableStateOf<List<String>>(emptyList()) }
  // 処理中表示(インジケータ表示用)
  var isLoading by remember { mutableStateOf(false) }

  Box(modifier = modifier.fillMaxSize()) {
    Image(
      bitmap = editBitmap.asImageBitmap(),
      contentDescription = "撮影画像",
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Fit // アスペクト比を保ち表示エリアにフィット
    )
    Row( // 画面下部のボタンリスト
      modifier = Modifier
        .align(Alignment.BottomCenter) // 画面下部中央に配置
        .padding(16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Button(onClick = { // テキストの認識のボタン
        isLoading = true // インジケータ表示
        recognizeText(bitmap) { resultBitmap, result ->
          // 認識結果を受け取り、状態変数にセットする。
          editBitmap = resultBitmap
          textList = result // 認識結果のリスト
          showSheet = true // ModalBottomSheetを表示
          isLoading = false // インジケータ表示解除
        }
      }) {
        Icon(imageVector = Icons.Filled.TextFields, contentDescription = "文字読み取り")
      }
      Button(onClick = { // バーコード認識のボタン
        isLoading = true // インジケータ表示
        recognizeBarcode(bitmap) { resultBitmap, result ->
          // 認識結果を受け取り、状態変数にセットする。
          editBitmap = resultBitmap
          textList = result // 認識結果のリスト
          showSheet = true // ModalBottomSheetを表示
          isLoading = false // インジケータ表示解除
        }
      }) {
        Icon(imageVector = Icons.Filled.QrCodeScanner, contentDescription = "バーコード読取り")
      }
      // 認識結果を再表示する
      Button(onClick = { showSheet = true }) {
        Icon(imageVector = Icons.Filled.Refresh, contentDescription = "再表示")
      }
      // カメラプレビューに戻る
      Button(onClick = onBack) {
        Icon(imageVector = Icons.Filled.CameraAlt, contentDescription = "カメラに戻る")
      }
    }
  }
  if (showSheet) { //  ModalBottomSheetを表示
    ModalBottomSheet(
      onDismissRequest = { showSheet = false },
      sheetState = sheetState
    ) {
      Card(
        modifier = Modifier.align(Alignment.CenterHorizontally)
      ) {
        Text(
          text = "*** 認識結果 ***",
          modifier = Modifier.padding(8.dp)
        )
      }
      Text(
        text = textList.joinToString("\n"),
        modifier = Modifier
          .padding(8.dp)
          .verticalScroll(rememberScrollState())
      )
    }
  } // ModalBottomSheetを表示の閉じカッコ

  // 処理中のインジケータ表示
  if (isLoading) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .clickable { /* 何もしない（画面タップが無効になる）*/ }
        .background(Color.Black.copy(alpha = 0.5f)),
      contentAlignment = Alignment.Center,
    ) {
      CircularProgressIndicator(color = Color.White)
    }
  }// 処理中のインジケータの閉じカッコ
} // RecognizeViewの閉じカッコ

// テキストの認識・加工関数
fun recognizeText(
  bitmap: Bitmap,
  onResult: (Bitmap, List<String>) -> Unit
) {
  // TextRecognizerを準備(日本語用)
  val recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
  // BitmapをMLKitが扱える形式に変換。回転補正は済んでいるので0度を指定。
  val image = InputImage.fromBitmap(bitmap, 0)

  // テキスト認識処理を開始 (処理は別スレッドで実行)
  recognizer.process(image).addOnSuccessListener { result -> // 認識成功時の処理
    // 元のBitmapを変更可能(mutable)な形式でコピー
    val resultBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    // Bitmapに青枠を描画するためのCanvasを用意
    val canvas = android.graphics.Canvas(resultBitmap)
    // 描画用のPaintを設定 (青色、枠線、太さ4f)
    val paint = android.graphics.Paint().apply {
      color = android.graphics.Color.BLUE
      style = android.graphics.Paint.Style.STROKE
      strokeWidth = 4f
    }

    // 認識したテキスト行を格納するリスト
    val results = mutableListOf<String>()

    // 認識結果のテキストブロックを繰り返し処理
    result.textBlocks.forEach { block ->
      // 全体を囲む枠線を書き込み
      block.boundingBox?.let { canvas.drawRect(it, paint) }
      // 行ごとに枠線を書き込み・結果リストに認識した文字を追加
      block.lines.forEach {
        it.boundingBox?.let { box -> canvas.drawRect(box, paint) }
        results.add(it.text)
      }
    }
    // 青枠を描画したBitmapと、認識したテキストのリストを返却
    onResult(resultBitmap, results)
  }.addOnFailureListener { e -> // 認識失敗時の処理
    onResult(bitmap, listOf("読み取り失敗: ${e.message}"))
  }
} // recognizeTextの閉じカッコ

// バーコードの認識・加工関数
fun recognizeBarcode(bitmap: Bitmap, onResult: (Bitmap, List<String>) -> Unit) {
  // バーコードスキャナを準備
  val scanner = BarcodeScanning.getClient()
  // BitmapをMLKitが扱える形式に変換。回転補正は済んでいるので0度を指定。
  val image = InputImage.fromBitmap(bitmap, 0)

  // バーコード認識処理を開始 (処理は別スレッドで実行)
  scanner.process(image).addOnSuccessListener { barcodes ->
    // 元のBitmapを変更可能(mutable)な形式でコピー
    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    // Bitmapに赤枠を描画するためのCanvasを用意
    val canvas = android.graphics.Canvas(mutableBitmap)
    // 描画用のPaintを設定 (赤色、枠線、太さ4f)
    val paint = android.graphics.Paint().apply {
      color = android.graphics.Color.RED
      style = android.graphics.Paint.Style.STROKE
      strokeWidth = 4f
    }

    // 認識したテキスト行を格納するリスト
    val results = mutableListOf<String>()

    // 認識結果のバーコードを検知回数分繰り返し処理
    barcodes.forEach { barcode ->
      // バーコードごとに枠線を書き込み
      barcode.boundingBox?.let { canvas.drawRect(it, paint) }
      // バーコードの値を結果リストに追加
      barcode.rawValue?.let {
        results.add(it)
      }
    }
    // 赤枠を描画したBitmapと、認識したバーコードを返却
    onResult(mutableBitmap, results)

  }.addOnFailureListener { e -> // 認識失敗時の処理
    onResult(bitmap, listOf("読み取り失敗: ${e.message}"))
  }
} // recognizeBarcodeの閉じカッコ

@Preview(showBackground = true)
@Composable
fun MainPreview() {
  PracticeCameraAppTheme {
    Main()
  }
}
