package com.application.presence.Screen.Components

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.application.presence.ML.QrAnalyzer
import com.application.presence.data.model.Profile
import com.application.presence.data.state.ScannerSubmissionState
import com.application.presence.viewmodel.ScannerViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*


@Composable
fun CustomBannerScreen(message:String) {
    // State to track if the banner should be visible
    var showErrorBanner by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {

        // The Banner UI
        AnimatedVisibility(visible = showErrorBanner) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Error: $message",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { showErrorBanner = false } // Dismiss the banner on click
                ) {
                    Text("OK", color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }

        // The Rest of your Screen UI
        Column(modifier = Modifier.padding(16.dp)) {
            Button(onClick = { showErrorBanner = true }) {
                Text("Simulate Error")
            }
        }
    }
}

@Composable
fun RequestCameraPermission(
    onGranted:@Composable () -> Unit
){
    val permission = Manifest.permission.CAMERA
    val context = LocalContext.current
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,permission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        granted = it
    }

    LaunchedEffect(Unit) {
        if(!granted) launcher.launch(permission)
    }

    if(granted) onGranted()
    else Text("Permission Denied")
}


@Composable
fun CameraScanner(
    viewModel: ScannerViewModel
) {
    val context = LocalContext.current
    val application = context.applicationContext
    //This lifecycle owner will be used to change from app screen to URL
    val lifecycleOwner = LocalLifecycleOwner.current
    //This we are using to make camera as object which we can use to control camera features, e.g. zoom, torch on or OFF
    var camera: Camera? by remember { mutableStateOf(null) }
    //Torch enabled or not
    var torchEnabled by remember { mutableStateOf(false) }
    //Zoom ratio value stored here
    var zoomRatio by remember { mutableFloatStateOf(1f) }

    //Add this for Zoom Ratio (if not needed then remove it)
    LaunchedEffect(zoomRatio, camera) {
        camera?.let { cam ->
            val zoomState = cam.cameraInfo.zoomState.value
            val minZoom = zoomState?.minZoomRatio ?: 1f
            val maxZoon = zoomState?.maxZoomRatio ?: 1f
            val safeZoom = zoomRatio.coerceIn(minZoom,maxZoon)
            cam.cameraControl.setZoomRatio(safeZoom)
        }
    }

    Box(modifier = Modifier.fillMaxSize()){
        AndroidView(
            //First of all we won't need this modifier but we are going to add pointInput which detects pointer change in modifier
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit){
                    detectTransformGestures { _,_, zoomchange, _->
                        zoomRatio *= zoomchange
                    }
                },

            factory = { ctx ->
                val previewView = PreviewView(ctx)

                val cameraProviderFuture =
                    ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)

                    val analyzer = ImageAnalysis.Builder().build()
                    analyzer.setAnalyzer(
                        ContextCompat.getMainExecutor(ctx),
                        QrAnalyzer { result ->
                            viewModel.onQrScanned(result, application = application as Application)
                        }
                    )

                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    cameraProvider.unbindAll()
                    //Doing this so will help store camera as object
                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        analyzer
                    )
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )

        //Checking that the device have torch or not
        if(camera?.cameraInfo?.hasFlashUnit() ?: false){
            //For flash this logic exists
            IconButton(
                onClick = {
                    torchEnabled = !torchEnabled
                    camera?.cameraControl?.enableTorch(torchEnabled)
                },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if(torchEnabled){
                        Icons.Filled.FlashlightOn
                    }else{
                        Icons.Filled.FlashlightOff
                    },
                    contentDescription = "Flashlight",
                    tint = Color.White
                )
            }
        }
    }

}