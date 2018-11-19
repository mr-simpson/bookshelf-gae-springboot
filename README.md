# Spring Boot based Bookshelf App
GCPのチュートリアルで紹介されている「[Bookshelfアプリ][bookshelf-app]」をSpring Bootで実装しました。
Google App Engineはスタンダード環境を利用するようにしています。

[bookshelf-app]: https://cloud.google.com/java/getting-started-appengine-standard/tutorial-app

## プロパティの設定
### Google Cloud Storageのバケット名
画像ファイルはGoogle Cloud Storageに保存するため、appengine-web.xmlに記述しているbookshelf.bucketプロパティにGoogle Cloud Storageのバケット名を設定。

`<property name="bookshelf.bucket" value=""/> <!-- set google cloud storage bucket name -->`

## 実行方法
### ローカル開発サーバー
`mvn appengine:run`

### App Engineへのデプロイ
`mvn appengine:deploy`
