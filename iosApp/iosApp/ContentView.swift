import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var statusBarHeight = {
        return UIApplication.shared.windows.first?.safeAreaInsets.top
    }
    var systemNavBarHeight = {
        return UIApplication.shared.windows.first?.safeAreaInsets.bottom
    }
    
    var body: some View {
        GeometryReader { geometry in
            VStack(spacing: 0) {
                Color.black
                    .frame(height: geometry.size.height / 2)
                Color(hex: 0x191A1D)
            }
            .ignoresSafeArea()

            ComposeView()
                .padding(.bottom, systemNavBarHeight())
                .padding(.top, statusBarHeight())
                .ignoresSafeArea(edges: .all)
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
        }
    }
}

