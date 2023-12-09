class A11yServiceNode {
  List<int>? depth;
  List<int>? bounds;
  String? id;
  String? text;
  String? className;
  String? packageName;
  String? description;
  bool? clickable;
  bool? scrollable;
  bool? editable;

  A11yServiceNode({
    this.depth,
    this.bounds,
    this.id,
    this.text,
    this.className,
    this.packageName,
    this.description,
    this.clickable,
    this.scrollable,
    this.editable,
  });

  A11yServiceNode copyWith({
    List<int>? depth,
    List<int>? bounds,
    String? id,
    String? text,
    String? className,
    String? packageName,
    String? description,
    bool? clickable,
    bool? scrollable,
    bool? editable,
  }) {
    return A11yServiceNode(
      depth: depth ?? this.depth,
      bounds: bounds ?? this.bounds,
      id: id ?? this.id,
      text: text ?? this.text,
      className: className ?? this.className,
      packageName: packageName ?? this.packageName,
      description: description ?? this.description,
      clickable: clickable ?? this.clickable,
      scrollable: scrollable ?? this.scrollable,
      editable: editable ?? this.editable,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'depth': depth,
      'bounds': bounds,
      'id': id,
      'text': text,
      'className': className,
      'packageName': packageName,
      'description': description,
      'clickable': clickable,
      'scrollable': scrollable,
      'editable': editable,
    };
  }

  factory A11yServiceNode.fromJson(Map<String, dynamic> json) {
    return A11yServiceNode(
      depth: (json['depth'] as List<dynamic>?)?.map((e) => e as int).toList(),
      bounds: (json['bounds'] as List<dynamic>?)?.map((e) => e as int).toList(),
      id: json['id'] as String?,
      text: json['text'] as String?,
      className: json['className'] as String?,
      packageName: json['packageName'] as String?,
      description: json['description'] as String?,
      clickable: json['clickable'] as bool?,
      scrollable: json['scrollable'] as bool?,
      editable: json['editable'] as bool?,
    );
  }

  @override
  String toString() => '$depth : $packageName / $className { $id | $text | $description | $bounds | $clickable | $scrollable | $editable }';

  @override
  int get hashCode => Object.hash(depth, bounds, id, text, className, packageName, description, clickable, scrollable, editable);

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is A11yServiceNode &&
          runtimeType == other.runtimeType &&
          depth == other.depth &&
          bounds == other.bounds &&
          id == other.id &&
          text == other.text &&
          className == other.className &&
          packageName == other.packageName &&
          description == other.description &&
          clickable == other.clickable &&
          scrollable == other.scrollable &&
          editable == other.editable;
}
