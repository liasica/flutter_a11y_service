class A11yServiceEvent {
  String? packageName;
  String? className;
  String? text;
  String? description;

  A11yServiceEvent({
    this.packageName,
    this.className,
    this.text,
    this.description,
  });

  A11yServiceEvent copyWith({
    String? packageName,
    String? className,
    String? text,
    String? description,
  }) {
    return A11yServiceEvent(
      packageName: packageName ?? this.packageName,
      className: className ?? this.className,
      text: text ?? this.text,
      description: description ?? this.description,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'packageName': packageName,
      'className': className,
      'text': text,
      'description': description,
    };
  }

  factory A11yServiceEvent.fromJson(Map<String, dynamic> json) {
    return A11yServiceEvent(
      packageName: json['packageName'] as String?,
      className: json['className'] as String?,
      text: json['text'] as String?,
      description: json['description'] as String?,
    );
  }

  @override
  String toString() => '$packageName / $className { $text | $description }';

  @override
  int get hashCode => Object.hash(packageName, className, text, description);

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is A11yServiceEvent && runtimeType == other.runtimeType && packageName == other.packageName && className == other.className && text == other.text && description == other.description;
}
